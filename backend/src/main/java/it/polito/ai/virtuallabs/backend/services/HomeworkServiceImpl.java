package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.HomeworkActionDTO;
import it.polito.ai.virtuallabs.backend.dtos.HomeworkDTO;
import it.polito.ai.virtuallabs.backend.dtos.PageDTO;
import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.entities.*;
import it.polito.ai.virtuallabs.backend.repositories.CourseRepository;
import it.polito.ai.virtuallabs.backend.repositories.HomeworkActionRepository;
import it.polito.ai.virtuallabs.backend.repositories.HomeworkRepository;
import it.polito.ai.virtuallabs.backend.repositories.StudentRepository;
import it.polito.ai.virtuallabs.backend.security.AuthenticatedEntityMapper;
import it.polito.ai.virtuallabs.backend.utils.GetterProxy;
import it.polito.ai.virtuallabs.backend.utils.ImageConverterEngine;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class HomeworkServiceImpl implements HomeworkService {

    @Autowired
    HomeworkRepository homeworkRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    HomeworkActionRepository homeworkActionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    GetterProxy getter;

    @Autowired
    AuthenticatedEntityMapper authenticatedEntityMapper;

    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    @Override
    public void addHomework(String courseCode, String title, Long dueDate, MultipartFile file) {
        Course course = getter.course(courseCode);
        
        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();
        if(!course.getEnabled())
            throw new CourseNotEnabledException();

        try{
            BufferedImage converted = ImageConverterEngine.convert(file);

            Timestamp now = new Timestamp(System.currentTimeMillis());
            Timestamp due = new Timestamp(dueDate);
            if(due.before(now)) {
                throw new HomeworkDueDateException();
            }
            Path coursePath = Paths.get( "uploads/homeworks/" + courseCode);
            if(!Files.exists(coursePath))
                Files.createDirectory(coursePath);
            Homework homework = Homework.builder()
                    .publicationDate(now)
                    .dueDate(due)
                    .title(title)
                    .build();
            course.addHomework(homework);

            homeworkRepository.save(homework);
            courseRepository.save(course);
            course.getStudents().forEach(s -> {
                HomeworkAction firstAction = new HomeworkAction();
                firstAction.setActionType(HomeworkAction.ActionType.NULL);
                firstAction.setDate(new Timestamp(System.currentTimeMillis()));
                firstAction.setHomework(homework);
                firstAction.setStudent(s);
                homeworkActionRepository.save(firstAction);
            });
            Files.deleteIfExists(coursePath.resolve(homework.getId().toString() + ".jpg"));
            ImageIO.write(converted, "jpg", coursePath.resolve(homework.getId().toString() + ".jpg").toFile());
        } catch (IOException e) {
            throw new FileHandlingException();
        }

    }

    @Override
    public HomeworkDTO getHomework(Long homeworkId) {
        Homework homework = getter.homework(homeworkId);
        if(!homework.getCourse().getEnabled()) {
            throw new CourseNotEnabledException();
        }
        try{

            Student authenticated = (Student) authenticatedEntityMapper.get();
            if(!authenticated.getCourses().contains(homework.getCourse()))
                throw new NotAllowedException();

        } catch (ClassCastException e) {
            if(!homework.getCourse().getProfessors().contains((Professor) authenticatedEntityMapper.get()))
                throw new NotAllowedException();
        }
        return modelMapper.map(homework, HomeworkDTO.class);
    }

    @Override
    public Resource getHomeworkText(Long homeworkId) {
        Homework homework = getter.homework(homeworkId);

        if(!homework.getCourse().getEnabled()) {
            throw new CourseNotEnabledException();
        }

        Path file = root.resolve("homeworks/" + homework.getCourse().getCode() + "/" + homework.getId() + ".jpg");
        try{
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable())
                throw new RuntimeException("Could not read the file!");

            try {
                Student authenticated = (Student) authenticatedEntityMapper.get();

                if(!authenticated.getCourses().contains(homework.getCourse()))
                    throw new NotAllowedException();

                List<HomeworkAction> actions = authenticated.getHomeworkActions().stream().filter(ha -> ha.getHomework().equals(homework)).sorted(byHomeworkActionDate).collect(Collectors.toList());

                if(actions.get(actions.size() -1).isNull()) {
                    HomeworkAction homeworkAction = new HomeworkAction();
                    homeworkAction.setDate(new Timestamp(System.currentTimeMillis()));
                    homeworkAction.setActionType(HomeworkAction.ActionType.READ);
                    homeworkAction.assignStudent(authenticated);
                    homeworkAction.assignHomework(homework);
                    homeworkActionRepository.save(homeworkAction);
                }
                return resource;
            } catch (ClassCastException e) {
                //Nel caso di un Professor
                if(!homework.getCourse().getProfessors().contains((Professor) authenticatedEntityMapper.get()))
                    throw new NotAllowedException();

                return resource;
            }
        } catch (MalformedURLException e) {
            throw new HomeworkNotFoundException();
        }
    }

    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    @Override
    public void deleteHomework(Long homeworkId) {
        Homework homework = getter.homework(homeworkId);
        if(!homework.getCourse().getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();
        if(!homework.getCourse().getEnabled()) {
            throw new CourseNotEnabledException();
        }
        Path file = root.resolve("homeworks/" + homework.getCourse().getCode() + "/" + homework.getId() + ".jpg");
        try {
            Files.deleteIfExists(file);
            homeworkRepository.delete(homework);
        } catch (IOException e) {
            throw new FileHandlingException();
        }
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Override
    public void addHomeworkDelivery(Long homeworkId, MultipartFile file) {
        Homework homework = getter.homework(homeworkId);

        if(!homework.getCourse().getEnabled()) {
            throw new CourseNotEnabledException();
        }
        Student authenticated = (Student) authenticatedEntityMapper.get();

        if(!authenticated.getCourses().contains(homework.getCourse()))
            throw new NotAllowedException();

        List<HomeworkAction> homeworkActions = authenticated.getHomeworkActions().stream().filter(ha -> ha.getHomework().equals(homework)).sorted(byHomeworkActionDate).collect(Collectors.toList());

        HomeworkAction lastAction = homeworkActions.get(homeworkActions.size() - 1);

        if(lastAction.getMark() != null || lastAction.isDelivery() || lastAction.isNull())
            throw new HomeworkActionNotAllowedException();

        Timestamp now = new Timestamp(System.currentTimeMillis());

        if(now.after(homework.getDueDate()))
            throw new HomeworkActionNotAllowedException();

        HomeworkAction homeworkAction = new HomeworkAction();
        homeworkAction.setDate(now);
        homeworkAction.setActionType(HomeworkAction.ActionType.DELIVERY);
        homeworkAction.assignHomework(homework);
        homeworkAction.assignStudent(authenticated);
        homeworkActionRepository.save(homeworkAction);

        try{
            Path coursePath = Paths.get( "uploads/homeworks/deliveries/" + homework.getId());
            if(!Files.exists(coursePath))
                Files.createDirectory(coursePath);

            BufferedImage converted = ImageConverterEngine.convert(file);
            Files.deleteIfExists(coursePath.resolve(homeworkAction.getId().toString() + ".jpg"));
            ImageIO.write(converted, "jpg", coursePath.resolve(homeworkAction.getId().toString() + ".jpg").toFile());

        } catch (IOException e) {
            throw new FileHandlingException();
        }

    }

    @Override
    public Resource getHomeworkDeliveryResource(Long homeworkDeliveryId) {
        HomeworkAction homeworkDelivery = getter.homeworkAction(homeworkDeliveryId);

        if(homeworkDelivery.isRead())
            throw new HomeworkActionNotAllowedException();

        if(!homeworkDelivery.getHomework().getCourse().getEnabled()) {
            throw new CourseNotEnabledException();
        }

        try{
            Student authenticated = (Student) authenticatedEntityMapper.get();
            if(!authenticated.equals(homeworkDelivery.getStudent()))
                throw new NotAllowedException();
        } catch (ClassCastException e) {
            Professor authenticated = (Professor) authenticatedEntityMapper.get();
            if(!authenticated.getCourses().contains(homeworkDelivery.getHomework().getCourse()))
                throw new NotAllowedException();
        }

        Path file = root.resolve("homeworks/deliveries/" + homeworkDelivery.getHomework().getId() + "/" + homeworkDelivery.getId() + ".jpg");
        try{
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable())
                throw new RuntimeException("Could not read the file!");
            return  resource;
        } catch (MalformedURLException e) {
            throw new FileHandlingException();
        }

    }

    @Override
    public HomeworkActionDTO getHomeworkAction(Long homeworkActionId) {
        HomeworkAction homeworkAction = getter.homeworkAction(homeworkActionId);

        if(!homeworkAction.getHomework().getCourse().getEnabled()) {
            throw new CourseNotEnabledException();
        }

        try{
            Student authenticated = (Student) authenticatedEntityMapper.get();
            if(!authenticated.equals(homeworkAction.getStudent()))
                throw new NotAllowedException();
        } catch (ClassCastException e) {
            if(!((Professor) authenticatedEntityMapper.get()).getCourses().contains(homeworkAction.getHomework().getCourse()))
                throw new NotAllowedException();
        }
        return modelMapper.map(homeworkAction, HomeworkActionDTO.class);
    }

    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    @Override
    public PageDTO<HomeworkActionDTO> getAllHomeworkActions(Long homeworkId, Integer page, Integer pageSize, String filterBy) {
        Homework homework = getter.homework(homeworkId);

        if(!homework.getCourse().getEnabled()) {
            throw new CourseNotEnabledException();
        }

        if(page < 0 || pageSize < 0)
            throw new InvalidPageException();

        if(!filterBy.equals("ALL") && !filterBy.equals("READ") && !filterBy.equals("NULL") && !filterBy.equals("DELIVERY") && !filterBy.equals("REVIEW"))
            throw new IllegalFilterRequestException();

        if(!homework.getCourse().getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        Page<HomeworkAction> actionPage = null;
        if(filterBy.equals("ALL")) {
            actionPage = homeworkActionRepository.findAllActions(
                    PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "date")),
                    homeworkId);
        } else {
                actionPage = homeworkActionRepository.findAllByHomeworkIdAndActionType(
                        PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "date")),
                        homeworkId, filterBy.equals("NULL") ? HomeworkAction.ActionType.NULL : filterBy.equals("DELIVERY") ? HomeworkAction.ActionType.DELIVERY : filterBy.equals("READ") ? HomeworkAction.ActionType.READ : HomeworkAction.ActionType.REVIEW);
        }

        List<HomeworkActionDTO> dtos = actionPage.stream()
                .map(ha -> modelMapper.map(ha, HomeworkActionDTO.class))
                .collect(Collectors.toList());

        return new PageDTO<>((int)actionPage.getTotalElements(), dtos);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<HomeworkActionDTO> getAuthenticatedStudentHomeworkActions(Long homeworkId) {
        Homework homework = getter.homework(homeworkId);

        if(!homework.getCourse().getEnabled()) {
            throw new CourseNotEnabledException();
        }

        if(!homework.getCourse().getStudents().contains((Student) authenticatedEntityMapper.get()))
            throw new NotAllowedException();


        List<HomeworkActionDTO> dtos = ((Student) authenticatedEntityMapper.get()).getHomeworkActions().stream()
                .filter(ha -> ha.getHomework().equals(homework))
                .sorted(byHomeworkActionDate)
                .map(ha -> modelMapper.map(ha, HomeworkActionDTO.class))
                .collect(Collectors.toList());
        Collections.reverse(dtos);
        return dtos;
    }


    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    @Override
    public List<HomeworkActionDTO> getStudentHomeworkActions(Long homeworkId, Long studentId) {
        Homework homework = getter.homework(homeworkId);

        if(!homework.getCourse().getEnabled()) {
            throw new CourseNotEnabledException();
        }

        if(!homework.getCourse().getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        Student student = getter.student(studentId);

        if(!homework.getCourse().getStudents().contains(student))
            throw new StudentNotEnrolledException();

        return student.getHomeworkActions().stream()
                .filter(ha -> ha.getHomework().equals(homework))
                .sorted(byHomeworkActionDate)
                .map(ha -> {
                    HomeworkActionDTO action = new HomeworkActionDTO();
                    action.setStudent(modelMapper.map(student, StudentDTO.class));
                    action.setId(ha.getId());
                    action.setDate(ha.getDate());
                    action.setActionType(ha.getActionType());
                    return action;
                })
                .collect(Collectors.toList());
    }

    private static final Path root = Paths.get("uploads");
    private static final Path homeworkDirectory = Paths.get("uploads/homeworks");
    private static final Path homeworkDeliveriesDirectory = Paths.get("uploads/homeworks/deliveries");
    private static final Path profilePictureDirectory = Paths.get("uploads/profile_pictures");

    @Bean
    public void initDirectory() {
        try {
            if(!Files.exists(root))
                Files.createDirectory(root);
            if(!Files.exists(homeworkDirectory))
                Files.createDirectory(homeworkDirectory);
            if(!Files.exists(homeworkDeliveriesDirectory))
                Files.createDirectory(homeworkDeliveriesDirectory);
            if(!Files.exists(profilePictureDirectory)) {
                Files.createDirectory(profilePictureDirectory);
                Files.createDirectory(profilePictureDirectory.resolve("student"));
                Files.createDirectory(profilePictureDirectory.resolve("professor"));
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    private Comparator<HomeworkAction> byHomeworkActionDate = new Comparator<HomeworkAction>() {
        public int compare(HomeworkAction a1, HomeworkAction a2) {
            return Long.valueOf(a1.getDate().getTime()).compareTo(a2.getDate().getTime());
        }
    };

}

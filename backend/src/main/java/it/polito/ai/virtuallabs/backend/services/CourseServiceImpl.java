package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.*;
import it.polito.ai.virtuallabs.backend.entities.*;
import it.polito.ai.virtuallabs.backend.repositories.*;
import it.polito.ai.virtuallabs.backend.security.AuthenticatedEntityMapper;
import it.polito.ai.virtuallabs.backend.utils.GetterProxy;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private HomeworkActionRepository homeworkActionRepository;

    @Autowired
    private AuthenticatedEntityMapper authenticatedEntityMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GetterProxy getter;

    @Override
    public CourseDTO getCourse(String courseCode) {
        Course course = getter.course(courseCode);

        AuthenticatedEntity authenticated = authenticatedEntityMapper.get();
        if(authenticated instanceof Professor && !course.getProfessors().contains(authenticated))
            throw new NotAllowedException();
        if(authenticated instanceof Student && !course.getStudents().contains(authenticated))
            throw new NotAllowedException();

        return modelMapper.map(course, CourseDTO.class);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public boolean addCourse(CourseDTO course) {
        Course c = modelMapper.map(course, Course.class);

        // check if course code exists already
        if(courseRepository.existsById(course.getCode()))
            return false;

        c.addProfessor((Professor) authenticatedEntityMapper.get());
        courseRepository.save(c);
        return true;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public void updateCourse(String courseCode, CourseDTO courseDTO) {
        Course course = getter.course(courseCode);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        course.setName(courseDTO.getName());
        course.setAcronym(courseDTO.getAcronym());
        course.setMinTeamMembers(courseDTO.getMinTeamMembers());
        course.setMaxTeamMembers(courseDTO.getMaxTeamMembers());
        course.setEnabled(courseDTO.getEnabled());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public void removeCourse(String courseCode) {
        Course course = getter.course(courseCode);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        course.removeAllProfessors();
        course.removeAllStudents();
        course.removeAllTeams();

        courseRepository.delete(course);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public boolean inviteProfessor(String courseCode, Long professorId) {
        Course course = getter.course(courseCode);
        Professor professor = getter.professor(professorId);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        // check if professor is teaching course already
        if(course.getProfessors().contains(professor))
            return false;

        course.addProfessor(professor);
        return true;
    }

    @Override
    public PageDTO<CourseStudentDTO> getEnrolledStudents(String courseCode, String sortField, String sortDirection, int page, int pageSize) {
        Course course = getter.course(courseCode);

        AuthenticatedEntity authenticated = authenticatedEntityMapper.get();
        if(authenticated instanceof Professor && !course.getProfessors().contains(authenticated))
            throw new NotAllowedException();

        if(page < 0 || pageSize < 0)
            throw new InvalidPageException();

        // get enrolled students, each with its own team information
        Page<CourseStudent> studentPage = studentRepository.findAllWithTeamByCoursesIsContaining(
                PageRequest.of(page, pageSize, Sort.by(
                        sortDirection.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortField
                )),
                course);

        return new PageDTO<>(course.getStudents().size(), studentPage.stream().map(cs ->
                new CourseStudentDTO(
                        modelMapper.map(cs.getStudent(), StudentDTO.class),
                        // team may be null
                        cs.getTeam() != null ? modelMapper.map(cs.getTeam(), TeamDTO.class) : null
                )
        ).collect(Collectors.toList()));
    }

    @Override
    public List<ProfessorDTO> getProfessors(String courseCode) {
        Course course = getter.course(courseCode);

        AuthenticatedEntity authenticated = authenticatedEntityMapper.get();
        if(authenticated instanceof Professor && !course.getProfessors().contains(authenticated))
            throw new NotAllowedException();
        if(authenticated instanceof Student && !course.getStudents().contains(authenticated))
            throw new NotAllowedException();

        return course.getProfessors()
                .stream()
                .map(p -> modelMapper.map(p, ProfessorDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamDTO> getTeams(String courseCode) {
        Course course = getter.course(courseCode);

        AuthenticatedEntity authenticated = authenticatedEntityMapper.get();
        if(authenticated instanceof Professor && !course.getProfessors().contains(authenticated))
            throw new NotAllowedException();
        if(authenticated instanceof Student && !course.getStudents().contains(authenticated))
            throw new NotAllowedException();

        return course.getTeams()
                .stream()
                .map(t -> modelMapper.map(t, TeamDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public VmModelDTO getVmModel(String courseCode) {
        VmModel vmModel = getter.course(courseCode).getVmModel();

        AuthenticatedEntity authenticated = authenticatedEntityMapper.get();
        if(authenticated instanceof Professor && !vmModel.getCourse().getProfessors().contains(authenticated))
            throw new NotAllowedException();
        if(authenticated instanceof Student && !vmModel.getCourse().getStudents().contains(authenticated))
            throw new NotAllowedException();

        if(vmModel == null)
            return null;

        return new VmModelDTO(vmModel.getId(), vmModel.getName(), null);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public boolean addStudentToCourse(Long studentId, String courseCode) {
        Student student = getter.student(studentId);
        Course course = getter.course(courseCode);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();
        if(!course.getEnabled())
            throw new CourseNotEnabledException();
        if(student.getCourses().contains(course))
            return false;

        //Assign null action to course homeworks
        course.getHomeworks().forEach(h -> {
            HomeworkAction homeworkAction = new HomeworkAction();
            homeworkAction.assignStudent(student);
            homeworkAction.assignHomework(h);
            homeworkAction.setActionType(HomeworkAction.ActionType.NULL);
            homeworkAction.setDate(new Timestamp(System.currentTimeMillis()));
            homeworkActionRepository.save(homeworkAction);
        });

        course.addStudent(student);
        return true;
    }

    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    @Override
    public List<Boolean> enrollAllViaCsv(MultipartFile csvFile, String courseCode) {
        Course course = getter.course(courseCode);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();
        if(!course.getEnabled())
            throw new CourseNotEnabledException();

        try {
            BufferedReader csvReader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()));
            List<Boolean> result = csvReader.lines()
                    .map(line -> {
                        Long studentId = Long.parseLong(line);
                        Student student = getter.student(studentId);
                        if(student.getCourses().contains(course))
                            return false;
                        course.addStudent(student);
                        return true;
                    })
                    .collect(Collectors.toList());
            csvReader.close();
            return result;
        } catch (IOException | NumberFormatException e) {
            throw new CsvFileErrorException();
        }


    }

    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    @Override
    public List<Boolean> unenrollAll(List<Long> studentsIds, String courseCode) {
        Course course = getter.course(courseCode);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();
        if(!course.getEnabled())
            throw new CourseNotEnabledException();

        return studentsIds.stream()
                .map(s -> {
                    Student stud = getter.student(s);

                    if(!stud.getCourses().contains(course))
                        return false;

                    course.removeStudent(stud);
                    return true;
                })
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public void enableCourse(String courseCode) {
        Course course = getter.course(courseCode);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        course.setEnabled(true);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public void disableCourse(String courseCode) {
        Course course = getter.course(courseCode);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        course.setEnabled(false);
    }

    @Override
    public List<HomeworkDTO> getHomeworks(String courseCode) {
        Course course = getter.course(courseCode);

        AuthenticatedEntity authenticated = authenticatedEntityMapper.get();
        if(authenticated instanceof Professor && !course.getProfessors().contains(authenticated))
            throw new NotAllowedException();
        if(authenticated instanceof Student && !course.getStudents().contains(authenticated))
            throw new NotAllowedException();

        return course.getHomeworks()
                .stream()
                .map(h -> modelMapper.map(h, HomeworkDTO.class))
                .sorted((h1, h2) -> Long.valueOf(h1.getDueDate().getTime()).compareTo(h2.getDueDate().getTime()))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public void unenrollAllStudents(String courseCode) {
        Course course = getter.course(courseCode);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();
        if(!course.getEnabled())
            throw new CourseNotEnabledException();

        course.removeAllStudents();
    }

    @Override
    public Boolean professorHasSignalPermission(String courseCode, Long professorId) {
        Course course = getter.course(courseCode);
        Professor professor = getter.professor(professorId);

        return course.getProfessors().contains(professor);
    }

}

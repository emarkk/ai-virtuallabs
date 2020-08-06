package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.HomeworkDTO;
import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Homework;
import it.polito.ai.virtuallabs.backend.entities.Professor;
import it.polito.ai.virtuallabs.backend.repositories.CourseRepository;
import it.polito.ai.virtuallabs.backend.repositories.HomeworkRepository;
import it.polito.ai.virtuallabs.backend.security.AuthenticatedEntityMapper;
import it.polito.ai.virtuallabs.backend.utils.GetterProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Optional;

@Transactional
@Service
public class HomeworkServiceImpl implements HomeworkService {

    @Autowired
    HomeworkRepository homeworkRepository;

    @Autowired
    CourseRepository courseRepository;

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
        if(!course.getEnabled()) {
            throw new CourseNotEnabledException();
        }
        try{
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
            Files.deleteIfExists(coursePath.resolve(homework.getId().toString() + ".jpg"));
            Files.copy(file.getInputStream(), coursePath.resolve(homework.getId().toString() + ".jpg"));
        } catch (IOException e) {
            throw new HomeworkUploadException();
        }

    }

    private static final Path root = Paths.get("uploads");
    private static final Path homeworkDir = Paths.get("uploads/homeworks");

    @Bean
    public void initDirectory() {
        try {
            if(!Files.exists(root))
                Files.createDirectory(root);
            if(!Files.exists(homeworkDir))
                Files.createDirectory(homeworkDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }
}

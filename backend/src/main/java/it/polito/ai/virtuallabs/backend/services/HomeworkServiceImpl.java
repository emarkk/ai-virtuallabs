package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.HomeworkDTO;
import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Homework;
import it.polito.ai.virtuallabs.backend.entities.Professor;
import it.polito.ai.virtuallabs.backend.repositories.CourseRepository;
import it.polito.ai.virtuallabs.backend.repositories.HomeworkRepository;
import it.polito.ai.virtuallabs.backend.security.AuthenticatedEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    AuthenticatedEntityMapper authenticatedEntityMapper;

    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    @Override
    public void storeHomework(MultipartFile file, String courseCode, long dueDate) {
        Optional<Course> courseOptional = courseRepository.findById(courseCode);

        if(courseOptional.isEmpty()) {
            throw new CourseNotFoundException();
        }
        Course c = courseOptional.get();
        if(!c.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();
        try{
            long now = System.currentTimeMillis();
            if(dueDate <= now) {
                throw new HomeworkDueDateException();
            }
            Timestamp due = new Timestamp(dueDate);
            String filename = now + "_"+ file.getOriginalFilename();
            Path coursePath = Paths.get( "uploads/homeworks/" + courseCode);
            if(!Files.exists(coursePath))
                Files.createDirectory(coursePath);
            Files.copy(file.getInputStream(), coursePath.resolve(filename));
            Homework homework = Homework.builder()
                    .assigned(new Timestamp(System.currentTimeMillis()))
                    .due(due)
                    .descriptionFilePath("uploads/homeworks/" + courseCode + "/" + filename)
                    .build();
            c.addHomework(homework);
            homeworkRepository.save(homework);
            courseRepository.save(c);
        } catch (IOException e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }
}

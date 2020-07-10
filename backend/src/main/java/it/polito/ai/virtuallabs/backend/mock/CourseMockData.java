package it.polito.ai.virtuallabs.backend.mock;

import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Professor;
import it.polito.ai.virtuallabs.backend.repositories.CourseRepository;
import it.polito.ai.virtuallabs.backend.repositories.ProfessorRepository;
import it.polito.ai.virtuallabs.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class CourseMockData {

    CourseRepository courseRepository;
    ProfessorRepository professorRepository;

    public CourseMockData(CourseRepository courseRepository, ProfessorRepository professorRepository) {
        this.courseRepository = courseRepository;
        this.professorRepository = professorRepository;
    }

    @Transactional
    public void add(String code, String name, String acronym, Integer min, Integer max, Boolean enabled) {
        Course course = new Course(code, name, acronym, min, max, enabled, null, null, null);
        courseRepository.save(course);
    }

    @Transactional
    public void prof(String courseCode, Long professorId) {
        Course course = courseRepository.getOne(courseCode);
        Professor professor = professorRepository.getOne(professorId);
        course.addProfessor(professor);
    }

    public void addCourses() {
        add("01NYHOV", "Applicazioni Internet", "AI", 2, 4, true);
        add("01QYDOV", "Information systems", "IS", 4, 7, true);
        add("01SQNOV", "Data spaces", "DS", 3, 5, true);
        add("02JGROV", "Computer system security", "CSS", 2, 5, false);
        add("02DUCOV", "Software Engineering II", "SE2", 3, 6, false);
    }

    public void addProfessors() {
        prof("01NYHOV", 14724L);
        prof("01QYDOV", 25842L);
        prof("01SQNOV", 19428L);
        prof("02JGROV", 24393L);
        prof("02DUCOV", 22294L);
    }

}

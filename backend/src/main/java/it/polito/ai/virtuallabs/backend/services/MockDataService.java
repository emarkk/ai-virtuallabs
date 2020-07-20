package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Professor;
import it.polito.ai.virtuallabs.backend.entities.Student;
import it.polito.ai.virtuallabs.backend.entities.User;
import it.polito.ai.virtuallabs.backend.repositories.CourseRepository;
import it.polito.ai.virtuallabs.backend.repositories.ProfessorRepository;
import it.polito.ai.virtuallabs.backend.repositories.StudentRepository;
import it.polito.ai.virtuallabs.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class MockDataService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfessorRepository professorRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public void addProfessor(Long id, String email, String password, String firstName, String lastName) {
        String username = "d" + id;
        String resumed = username + " " + firstName.toLowerCase() + " " + lastName.toLowerCase();
        Professor professor = new Professor(id, email, firstName, lastName, false, resumed, new ArrayList<>());
        User user = new User(id, username, email, passwordEncoder.encode(password), true, new Timestamp(System.currentTimeMillis()), null, List.of("ROLE_PROFESSOR"));
        userRepository.save(user);
        professorRepository.save(professor);
    }

    public void addProfessors() {
        addProfessor(14724L, "alessio.defranco@polito.it", "password", "Alessio", "De Franco");
        addProfessor(18329L, "giada.caputi@polito.it", "password", "Giada", "Caputi");
        addProfessor(25842L, "benedetta.tirone@polito.it", "password", "Benedetta", "Tirone");
        addProfessor(19428L, "fabio.capello@polito.it", "password", "Fabio", "Capello");
        addProfessor(13429L, "lorenzo.dedominicis@polito.it", "password", "Lorenzo", "De Dominicis");
        addProfessor(24393L, "leonardo.tirato@polito.it", "password", "Leonardo", "Tirato");
        addProfessor(22294L, "fabiana.canella@polito.it", "password", "Fabiana", "Canella");
    }

    public void addStudent(Long id, String email, String password, String firstName, String lastName) {
        String username = "s" + id;
        String resumed = username + " " + firstName.toLowerCase() + " " + lastName.toLowerCase();
        Student student = new Student(id, email, firstName, lastName, false, resumed, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        User user = new User(id, username, email, passwordEncoder.encode(password), true, new Timestamp(System.currentTimeMillis()), null, List.of("ROLE_STUDENT"));
        userRepository.save(user);
        studentRepository.save(student);
    }

    public void enrollStudent(Long studentId, String courseCode) {
        Student student = studentRepository.getOne(studentId);
        Course course = courseRepository.getOne(courseCode);
        course.addStudent(student);
    }

    public void addStudents() {
        addStudent(248530L, "s248530@studenti.polito.it", "password", "Francesco", "Mantovani");
        addStudent(251129L, "s251129@studenti.polito.it", "password", "Martina", "Preta");
        addStudent(259434L, "s259434@studenti.polito.it", "password", "Sara", "Bergamini");
        addStudent(218506L, "s218506@studenti.polito.it", "password", "Davide", "Sottilini");
        addStudent(266394L, "s266394@studenti.polito.it", "password", "Filippo", "Preziosi");
        addStudent(250354L, "s250354@studenti.polito.it", "password", "Giorgia", "Trevisan");
        addStudent(243044L, "s243044@studenti.polito.it", "password", "Deborah", "De Luigi");
        addStudent(210293L, "s210293@studenti.polito.it", "password", "Renzo", "D'Ottavio");
        addStudent(240394L, "s240394@studenti.polito.it", "password", "Alessandro", "Sottile");
        addStudent(209341L, "s209341@studenti.polito.it", "password", "Fabiana", "Rai");
        addStudent(245458L, "s245458@studenti.polito.it", "password", "Francesca", "Popoli");
        addStudent(254390L, "s254390@studenti.polito.it", "password", "Vittoria", "De Franco");
        addStudent(202123L, "s202123@studenti.polito.it", "password", "Francesco", "De Carlo");
        addStudent(234943L, "s234943@studenti.polito.it", "password", "Martina", "Prezzi");
        addStudent(229302L, "s229302@studenti.polito.it", "password", "Alessandro", "Trentino");
    }

    public void enrollStudents() {
        enrollStudent(248530L, "01QYDOV"); enrollStudent(248530L, "02DUCOV");
        enrollStudent(251129L, "01NYHOV");
        enrollStudent(218506L, "01SQNOV"); enrollStudent(218506L, "02JGROV"); enrollStudent(218506L, "02DUCOV");
        enrollStudent(266394L, "01SQNOV");
        enrollStudent(243044L, "01QYDOV"); enrollStudent(243044L, "01SQNOV");
        enrollStudent(210293L, "01NYHOV"); enrollStudent(210293L, "02DUCOV");
        enrollStudent(240394L, "02JGROV");
        enrollStudent(245458L, "01QYDOV"); enrollStudent(245458L, "02JGROV"); enrollStudent(245458L, "01SQNOV");
        enrollStudent(254390L, "01QYDOV"); enrollStudent(254390L, "02JGROV");
        enrollStudent(202123L, "02DUCOV");
        enrollStudent(234943L, "01NYHOV"); enrollStudent(234943L, "01SQNOV"); enrollStudent(234943L, "02DUCOV");
        enrollStudent(229302L, "01NYHOV");
    }

    public void addCourse(String code, String name, String acronym, Integer min, Integer max, Boolean enabled) {
        Course course = new Course(code, name, acronym, min, max, enabled, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        courseRepository.save(course);
    }

    public void profToCourse(String courseCode, Long professorId) {
        Course course = courseRepository.getOne(courseCode);
        Professor professor = professorRepository.getOne(professorId);
        course.addProfessor(professor);
    }

    public void addCourses() {
        addCourse("01NYHOV", "Applicazioni Internet", "AI", 2, 4, true);
        addCourse("01QYDOV", "Information systems", "IS", 1, 7, true);
        addCourse("01SQNOV", "Data spaces", "DS", 3, 5, true);
        addCourse("02JGROV", "Computer system security", "CSS", 2, 5, false);
        addCourse("02DUCOV", "Software Engineering II", "SE2", 3, 6, false);
    }

    public void addProfessorsToCourses() {
        profToCourse("01NYHOV", 14724L);
        profToCourse("01QYDOV", 25842L);
        profToCourse("01SQNOV", 19428L);
        profToCourse("02JGROV", 24393L);
        profToCourse("02DUCOV", 22294L);
    }

    public void insertMockData() {
        addProfessors();
        addStudents();
        addCourses();

        addProfessorsToCourses();
        enrollStudents();
    }

}

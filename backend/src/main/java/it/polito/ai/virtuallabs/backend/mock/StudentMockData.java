package it.polito.ai.virtuallabs.backend.mock;

import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Student;
import it.polito.ai.virtuallabs.backend.entities.User;
import it.polito.ai.virtuallabs.backend.repositories.CourseRepository;
import it.polito.ai.virtuallabs.backend.repositories.ProfessorRepository;
import it.polito.ai.virtuallabs.backend.repositories.StudentRepository;
import it.polito.ai.virtuallabs.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class StudentMockData {

    UserRepository userRepository;
    StudentRepository studentRepository;
    CourseRepository courseRepository;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public StudentMockData(UserRepository userRepository, StudentRepository studentRepository,
                           CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void add(Long id, String email, String password, String firstName, String lastName) {
        String username = "s" + id;
        String resumed = username + " " + firstName.toLowerCase() + " " + lastName.toLowerCase();
        Student student = new Student(id, email, firstName, lastName, false, resumed, new ArrayList<>(), new ArrayList<>());
        User user = new User(id, username, email, passwordEncoder.encode(password), true, new Timestamp(System.currentTimeMillis()), null, List.of("ROLE_STUDENT"));
        userRepository.save(user);
        studentRepository.save(student);
    }

    @Transactional
    public void enroll(Long studentId, String courseCode) {
        Student student = studentRepository.getOne(studentId);
        Course course = courseRepository.getOne(courseCode);
        course.addStudent(student);
    }

    public void addStudents() {
        add(248530L, "s248530@studenti.polito.it", "password", "Francesco", "Mantovani");
        add(251129L, "s251129@studenti.polito.it", "password", "Martina", "Preta");
        add(259434L, "s259434@studenti.polito.it", "password", "Sara", "Bergamini");
        add(218506L, "s218506@studenti.polito.it", "password", "Davide", "Sottilini");
        add(266394L, "s266394@studenti.polito.it", "password", "Filippo", "Preziosi");
        add(250354L, "s250354@studenti.polito.it", "password", "Giorgia", "Trevisan");
        add(243044L, "s243044@studenti.polito.it", "password", "Deborah", "De Luigi");
        add(210293L, "s210293@studenti.polito.it", "password", "Renzo", "D'Ottavio");
        add(240394L, "s240394@studenti.polito.it", "password", "Alessandro", "Sottile");
        add(209341L, "s209341@studenti.polito.it", "password", "Fabiana", "Rai");
        add(245458L, "s245458@studenti.polito.it", "password", "Francesca", "Popoli");
        add(254390L, "s254390@studenti.polito.it", "password", "Vittoria", "De Franco");
        add(202123L, "s202123@studenti.polito.it", "password", "Francesco", "De Carlo");
        add(234943L, "s234943@studenti.polito.it", "password", "Martina", "Prezzi");
        add(229302L, "s229302@studenti.polito.it", "password", "Alessandro", "Trentino");
    }

    public void enrollStudents() {
        enroll(248530L, "01QYDOV"); enroll(248530L, "02DUCOV");
        enroll(251129L, "01NYHOV");
        enroll(218506L, "01SQNOV"); enroll(218506L, "02JGROV"); enroll(218506L, "02DUCOV");
        enroll(266394L, "01SQNOV");
        enroll(243044L, "01QYDOV"); enroll(243044L, "01SQNOV");
        enroll(210293L, "01NYHOV"); enroll(210293L, "02DUCOV");
        enroll(240394L, "02JGROV");
        enroll(245458L, "01QYDOV"); enroll(245458L, "02JGROV"); enroll(245458L, "01SQNOV");
        enroll(254390L, "01QYDOV"); enroll(254390L, "02JGROV");
        enroll(202123L, "02DUCOV");
        enroll(234943L, "01NYHOV"); enroll(234943L, "01SQNOV"); enroll(234943L, "02DUCOV");
        enroll(229302L, "01NYHOV");
    }

}

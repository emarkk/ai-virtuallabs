package it.polito.ai.virtuallabs.backend.mock;

import it.polito.ai.virtuallabs.backend.repositories.CourseRepository;
import it.polito.ai.virtuallabs.backend.repositories.ProfessorRepository;
import it.polito.ai.virtuallabs.backend.repositories.StudentRepository;
import it.polito.ai.virtuallabs.backend.repositories.UserRepository;

public class MockData {

    UserRepository userRepository;
    ProfessorRepository professorRepository;
    StudentRepository studentRepository;
    CourseRepository courseRepository;

    public MockData(UserRepository userRepository, ProfessorRepository professorRepository,
             StudentRepository studentRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.professorRepository = professorRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    public void insertMockData() {
        ProfessorMockData professors = new ProfessorMockData(this.userRepository, this.professorRepository);
        StudentMockData students = new StudentMockData(this.userRepository, this.studentRepository,
                this.courseRepository);
        CourseMockData courses = new CourseMockData(this.courseRepository, this.professorRepository);

        professors.addProfessors();
        students.addStudents();
        courses.addCourses();

        courses.addProfessors();
        students.enrollStudents();
    }

}

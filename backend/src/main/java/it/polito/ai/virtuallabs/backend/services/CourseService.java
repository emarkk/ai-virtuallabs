package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.CourseDTO;
import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamDTO;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface CourseService {
    boolean addCourse(CourseDTO course);
    Optional<CourseDTO> getCourse(String courseCode);
    List<CourseDTO> getAllCourses();
    List<StudentDTO> getEnrolledStudents(String courseCode);
    List<TeamDTO> getTeams(String courseCode);
    List<StudentDTO> getStudentsInTeams(String courseCode);
    List<StudentDTO> getStudentsNotInTeams(String courseCode);
    boolean addStudentToCourse(Long studentId, String courseCode);
    List<Boolean> enrollAll(List<Long> studentsIds, String courseCode);
    void enableCourse(String courseCode);
    void disableCourse(String courseCode);
}

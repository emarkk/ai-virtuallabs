package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.*;

import java.util.List;
import java.util.Optional;

public interface CourseService {
    boolean addCourse(CourseDTO course);
    Optional<CourseDTO> getCourse(String courseCode);
    List<CourseDTO> getAllCourses();
    void updateCourse(String courseCode, CourseDTO course);
    void removeCourse(String courseCode);
    boolean inviteProfessor(String courseCode, Long professorId);
    List<StudentDTO> getEnrolledStudents(String courseCode);
    List<ProfessorDTO> getProfessors(String courseCode);
    List<TeamDTO> getTeams(String courseCode);
    List<StudentDTO> getStudentsInTeams(String courseCode);
    List<StudentDTO> getStudentsNotInTeams(String courseCode);
    boolean addStudentToCourse(Long studentId, String courseCode);
    List<Boolean> enrollAll(List<Long> studentsIds, String courseCode);
    List<Boolean> unenrollAll(List<Long> studentsIds, String courseCode);
    void enableCourse(String courseCode);
    void disableCourse(String courseCode);
    List<HomeworkDTO> getHomeworksData(String courseCode);
}

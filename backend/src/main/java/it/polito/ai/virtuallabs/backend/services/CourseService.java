package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface CourseService {
    boolean addCourse(CourseDTO course);
    CourseDTO getCourse(String courseCode);
    void updateCourse(String courseCode, CourseDTO course);
    void removeCourse(String courseCode);
    boolean inviteProfessor(String courseCode, Long professorId);
    PageDTO<CourseStudentDTO> getEnrolledStudents(String courseCode, String sortField, String sortDirection, int page, int pageSize);
    List<ProfessorDTO> getProfessors(String courseCode);
    List<TeamDTO> getTeams(String courseCode);
    VmModelDTO getVmModel(String courseCode);
    boolean addStudentToCourse(Long studentId, String courseCode);
    List<Boolean> enrollAllViaCsv(MultipartFile csvFile, String courseCode);
    List<Boolean> unenrollAll(List<Long> studentsIds, String courseCode);
    void enableCourse(String courseCode);
    void disableCourse(String courseCode);
    List<HomeworkDTO> getHomeworks(String courseCode);
    void unenrollAllStudents(String courseCode);
    Boolean professorHasSignalPermission(String courseCode, Long professorId);
}

package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.CourseDTO;
import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    StudentDTO getStudent(Long studentId);
    List<StudentDTO> search(String q, String course, String excludeCourse, List<Long> excludeIds);
    List<CourseDTO> getCoursesForStudent(Long studentId);
    List<TeamDTO> getTeamsForStudent(Long studentId);
    List<TeamDTO> getTeamsForStudent(Long studentId, String courseCode);
    void addPicture(Long id, MultipartFile file);
    Resource getPicture(Long id);
}

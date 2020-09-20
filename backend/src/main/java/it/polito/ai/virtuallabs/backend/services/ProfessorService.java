package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.CourseDTO;
import it.polito.ai.virtuallabs.backend.dtos.ProfessorDTO;
import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProfessorService {
    ProfessorDTO getProfessor(Long professorId);
    List<ProfessorDTO> search(String q, String excludeCourse);
    List<CourseDTO> getCoursesForProfessor(Long professorId);
    void addPicture(Long id, MultipartFile file);
    Resource getPicture(Long id);
}

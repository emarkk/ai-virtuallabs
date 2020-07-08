package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.CourseDTO;

import java.util.List;
import java.util.Optional;

public interface ProfessorService {
    Optional<ProfessorDTO> getProfessor(Long professorId);
    List<ProfessorDTO> getAllProfessors();
    List<CourseDTO> getCoursesForProfessor(Long professorId);
}

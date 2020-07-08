package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.CourseDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamDTO;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    Optional<StudentDTO> getStudent(Long studentId);
    List<StudentDTO> getAllStudents();
    List<CourseDTO> getCoursesForStudent(Long studentId);
    List<TeamDTO> getTeamsForStudent(Long studentId);
}

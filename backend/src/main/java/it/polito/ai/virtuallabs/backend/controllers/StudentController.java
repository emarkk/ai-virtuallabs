package it.polito.ai.virtuallabs.backend.controllers;

import it.polito.ai.virtuallabs.backend.dtos.CourseDTO;
import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamDTO;
import it.polito.ai.virtuallabs.backend.services.StudentNotFoundException;
import it.polito.ai.virtuallabs.backend.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping({ "", "/" })
    public List<StudentDTO> all() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public StudentDTO getOne(@PathVariable("id") Long id) {
        Optional<StudentDTO> studentOptional = studentService.getStudent(id);

        if(studentOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student '" + id + "' not found");

        return studentOptional.get();
    }

    @GetMapping("/{id}/courses")
    public List<CourseDTO> getCourses(@PathVariable("id") Long id) {
        try {
            return studentService.getCoursesForStudent(id);
        } catch(StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student '" + id + "' not found");
        }
    }

    @GetMapping("/{id}/teams")
    public List<TeamDTO> getTeams(@PathVariable("id") Long id) {
        try {
            return studentService.getTeamsForStudent(id);
        } catch(StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student '" + id + "' not found");
        }
    }

}

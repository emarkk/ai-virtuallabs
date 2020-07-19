package it.polito.ai.virtuallabs.backend.controllers;

import it.polito.ai.virtuallabs.backend.dtos.CourseDTO;
import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamDTO;
import it.polito.ai.virtuallabs.backend.services.CourseNotEnabledException;
import it.polito.ai.virtuallabs.backend.services.CourseNotFoundException;
import it.polito.ai.virtuallabs.backend.services.StudentNotFoundException;
import it.polito.ai.virtuallabs.backend.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
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

    @GetMapping("/search")
    public List<StudentDTO> searchStudents(
            @RequestParam(name = "q") String q,
            @RequestParam(name = "excludeCourse", required = false) String exclude,
            @RequestParam(name = "includeCourse", required = false) String include,
            @RequestParam(name = "excludeIds", required = false) List<Long> ids,
            @RequestParam(name = "teamed", required = false) Boolean teamed) throws CourseNotFoundException, CourseNotEnabledException {
        try {
            return studentService.getOrderedSearchResult(q, exclude, include, ids, teamed);
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course Not Found");
        } catch (CourseNotEnabledException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course Not Enabled");
        }
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

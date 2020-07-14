package it.polito.ai.virtuallabs.backend.controllers;

import it.polito.ai.virtuallabs.backend.dtos.CourseDTO;
import it.polito.ai.virtuallabs.backend.dtos.ProfessorDTO;
import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.services.CourseNotFoundException;
import it.polito.ai.virtuallabs.backend.services.ProfessorNotFoundException;
import it.polito.ai.virtuallabs.backend.services.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/professors")
public class ProfessorController {

    @Autowired
    private ProfessorService professorService;

    @GetMapping({ "", "/" })
    public List<ProfessorDTO> all() {
        return professorService.getAllProfessors();
    }

    @GetMapping("/{id}")
    public ProfessorDTO getOne(@PathVariable("id") Long id) {
        Optional<ProfessorDTO> professorOptional = professorService.getProfessor(id);

        if(professorOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor '" + id + "' not found");

        return professorOptional.get();
    }

    @GetMapping("/search")
    public List<ProfessorDTO> searchProfessors(@RequestParam(name = "q") String q, @RequestParam(name = "excludeCourse", required = false) String exclude) throws CourseNotFoundException {
        try {
            return professorService.getOrderedSearchResult(q, exclude);
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exclude + ": Course Not Found");
        }
    }

    @GetMapping("/{id}/courses")
    public List<CourseDTO> getCourses(@PathVariable("id") Long id) {
        try {
            return professorService.getCoursesForProfessor(id);
        } catch(ProfessorNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor '" + id + "' not found");
        }
    }

}

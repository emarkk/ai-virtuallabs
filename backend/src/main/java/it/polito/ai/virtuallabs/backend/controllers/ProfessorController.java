package it.polito.ai.virtuallabs.backend.controllers;

import it.polito.ai.virtuallabs.backend.dtos.CourseDTO;
import it.polito.ai.virtuallabs.backend.dtos.ProfessorDTO;
import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/professors")
public class ProfessorController {

    @Autowired
    private ProfessorService professorService;

    @GetMapping("/{id}")
    public ProfessorDTO getProfessor(@PathVariable("id") Long id) {
        try{
            return professorService.getProfessor(id);
        } catch (ProfessorNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor '" + id + "' not found");
        }
    }

    @GetMapping("/search")
    public List<ProfessorDTO> searchProfessors(
            @RequestParam(name = "q") String q,
            @RequestParam(name = "excludeCourse", required = false) String excludeCourse)
            throws CourseNotFoundException {
        try {
            return professorService.search(q, excludeCourse);
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course '" + excludeCourse + "' not found");
        }
    }

    @GetMapping("/{id}/courses")
    public List<CourseDTO> getCoursesForProfessor(@PathVariable("id") Long id) {
        try {
            return professorService.getCoursesForProfessor(id);
        } catch(ProfessorNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor '" + id + "' not found");
        } catch (NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient Authorization");
        }
    }

    @PostMapping("/{id}/picture")
    @ResponseStatus(HttpStatus.CREATED)
    public void addPicture(@PathVariable(name = "id") Long id, @RequestParam("file") MultipartFile file) {
        try {
            professorService.addPicture(id, file);
        } catch (FileHandlingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        } catch (ProfessorNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor '" + id + "' not found");
        } catch (NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        }
    }

    @GetMapping("/{id}/picture")
    @ResponseBody
    public ResponseEntity<Resource> getPicture(@PathVariable(name = "id") Long id) {
        try {
            Resource file = professorService.getPicture(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
        } catch (FileHandlingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        } catch (ProfessorNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor '" + id + "' not found");
        } catch (ProfilePictureNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile picture Not Found");
        }
    }

}

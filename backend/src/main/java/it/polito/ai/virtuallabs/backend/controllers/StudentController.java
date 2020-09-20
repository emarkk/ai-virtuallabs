package it.polito.ai.virtuallabs.backend.controllers;

import it.polito.ai.virtuallabs.backend.dtos.CourseDTO;
import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamDTO;
import it.polito.ai.virtuallabs.backend.services.*;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/{id}")
    public StudentDTO getStudent(@PathVariable("id") Long id) {
        try {
            return studentService.getStudent(id);
        } catch (StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student '" + id + "' not found");
        }
    }

    @GetMapping("/search")
    public List<StudentDTO> searchStudents(
            @RequestParam(name = "q") String q,
            @RequestParam(name = "course", required = false) String course,
            @RequestParam(name = "teamed", required = false) Boolean teamed,
            @RequestParam(name = "excludeCourse", required = false) String excludeCourse,
            @RequestParam(name = "excludeIds", required = false) List<Long> excludeIds)
            throws CourseNotFoundException, CourseNotEnabledException {
        try {
            return studentService.search(q, course, teamed, excludeCourse, excludeIds);
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course Not Found");
        } catch (CourseNotEnabledException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course Not Enabled");
        }
    }

    @GetMapping("/{id}/courses")
    public List<CourseDTO> getCoursesForStudent(@PathVariable("id") Long id) {
        try {
            return studentService.getCoursesForStudent(id);
        } catch(StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student '" + id + "' not found");
        } catch (NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Action Not Allowed");
        }
    }

    @GetMapping("/{id}/teams")
    public List<TeamDTO> getTeamsForStudent(@PathVariable("id") Long id, @RequestParam(name = "course", required = false) String courseCode) {
        try {
            if(courseCode != null)
                return studentService.getTeamsForStudent(id, courseCode);

            return studentService.getTeamsForStudent(id);
        } catch(StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student '" + id + "' not found");
        } catch(CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course '" + courseCode + "' not found");
        } catch (NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Action Not Allowed");
        }
    }

    @PostMapping("/{id}/picture")
    @ResponseStatus(HttpStatus.CREATED)
    public void addPicture(@PathVariable(name = "id") Long id, @RequestParam("file") MultipartFile file) {
        try {
            studentService.addPicture(id, file);
        } catch (FileHandlingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        } catch (StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student '" + id + "' not found");
        } catch (NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        }
    }

    @GetMapping("/{id}/picture")
    @ResponseBody
    public ResponseEntity<Resource> getPicture(@PathVariable(name = "id") Long id) {
        try {
            Resource file = studentService.getPicture(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
        } catch (FileHandlingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        } catch (StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student '" + id + "' not found");
        } catch (ProfilePictureNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile picture Not Found");
        }
    }

}

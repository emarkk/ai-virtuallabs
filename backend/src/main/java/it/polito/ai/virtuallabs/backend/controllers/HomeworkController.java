package it.polito.ai.virtuallabs.backend.controllers;

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

import java.util.Map;

@RestController
@RequestMapping("/api/homeworks")
public class HomeworkController {
    @Autowired
    HomeworkService homeworkService;

    @PostMapping({ "", "/" })
    @ResponseStatus(HttpStatus.CREATED)
    public void addHomework(@RequestParam Map<String, String> input, @RequestParam("file") MultipartFile file) {
        if(!input.containsKey("courseCode") || !input.containsKey("dueDate") || !input.containsKey("title")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
        }
        try {
            homeworkService.addHomework(input.get("courseCode"), input.get("title"), Long.parseLong(input.get("dueDate")), file);
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course Not Found");
        } catch (CourseNotEnabledException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course Not Enabled");
        } catch (HomeworkDueDateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Due Date is not correct");
        } catch (NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Action Not Allowed");
        } catch (FileHandlingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Sever Error");
        }

    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Resource> getHomework(@PathVariable(name = "id") Long homeworkId) {
        try {
            Resource file = homeworkService.getHomework(homeworkId);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
        } catch (HomeworkNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Homework Not Found");
        } catch (NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Action Not Allowed");
        } catch (CourseNotEnabledException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course Not Enabled");
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteHomework(@PathVariable(name = "id") Long homeworkId) {
        try{
            homeworkService.deleteHomework(homeworkId);
        } catch (HomeworkNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Homework Not Found");
        } catch (NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Action Not Allowed");
        } catch (CourseNotEnabledException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course Not Enabled");
        } catch (FileHandlingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Sever Error");
        }
    }

    @PostMapping("/{id}/delivery")
    public void addHomeworkDelivery(@PathVariable(name = "id") Long homeworkId, @RequestParam("file") MultipartFile file) {
        try{
            homeworkService.addHomeworkDelivery(homeworkId, file);
        } catch (HomeworkNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Homework Not Found");
        } catch (NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Action Not Allowed");
        } catch (CourseNotEnabledException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course Not Enabled");
        } catch (HomeworkActionNotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Homework Action Not Allowed");
        } catch (FileHandlingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Sever Error");
        }
    }
}

package it.polito.ai.virtuallabs.backend.controllers;

import it.polito.ai.virtuallabs.backend.dtos.HomeworkActionDTO;
import it.polito.ai.virtuallabs.backend.dtos.HomeworkDTO;
import it.polito.ai.virtuallabs.backend.dtos.PageDTO;
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
    public HomeworkDTO getHomework(@PathVariable(name = "id") Long homeworkId) {
        try {
            return homeworkService.getHomework(homeworkId);
        } catch (HomeworkNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Homework Not Found");
        } catch (NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Action Not Allowed");
        } catch (CourseNotEnabledException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course Not Enabled");
        }
    }


    @GetMapping("/{id}/text")
    @ResponseBody
    public ResponseEntity<Resource> getHomeworkText(@PathVariable(name = "id") Long homeworkId) {
        try {
            Resource file = homeworkService.getHomeworkText(homeworkId);
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

    @GetMapping("/{id}/actions")
    public PageDTO<HomeworkActionDTO> getAllHomeworkActions(
            @PathVariable(name = "id") Long homeworkId,
            @RequestParam(name = "filterBy", required = false, defaultValue = "ALL") String filterBy,
            @RequestParam(name = "page", required = false, defaultValue = "0") String page,
            @RequestParam(name = "pageSize", required = false, defaultValue = "15") String pageSize) {
        try{
            return homeworkService.getAllHomeworkActions(homeworkId, Integer.parseInt(page), Integer.parseInt(pageSize), filterBy);
        } catch (HomeworkNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Homework Not Found");
        } catch (CourseNotEnabledException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course Not Enabled");
        } catch (NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Action Not Allowed");
        }  catch (InvalidPageException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Page or pageSize Field Not Valid");
        } catch (IllegalFilterRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal Filter Requested");
        }
    }

    @GetMapping("/{id}/actions/{studentId}")
    public List<HomeworkActionDTO> getStudentHomeworkActions(@PathVariable(name = "id") Long homeworkId, @PathVariable(name = "studentId") Long studentId) {
        try{
            return homeworkService.getStudentHomeworkActions(homeworkId, studentId);
        } catch (HomeworkNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Homework Not Found");
        } catch (CourseNotEnabledException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course Not Enabled");
        } catch (NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Action Not Allowed");
        } catch(StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student '" + studentId + "' not found");
        }  catch(StudentNotEnrolledException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student is not enrolled in course");
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

    @PostMapping("/{id}/review/{actionId}")
    public void addHomeworkReview(@PathVariable(name = "id") Long homeworkId, @PathVariable(name = "actionId") Long actionId, @RequestParam("file") MultipartFile file, @RequestParam(name = "mark", required = false) Integer mark) {
        try{
            homeworkService.addHomeworkReview(homeworkId, actionId, file, mark);
        } catch (HomeworkNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Homework Not Found");
        } catch (HomeworkActionNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Homework Action Not Found");
        } catch (NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Action Not Allowed");
        } catch (CourseNotEnabledException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course Not Enabled");
        } catch (HomeworkActionNotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Homework Action Not Allowed");
        } catch (FileHandlingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Sever Error");
        } catch (IllegalMarkException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mark Not Allowed");
        }
    }

    @GetMapping("/actions/{id}/resource")
    @ResponseBody
    public ResponseEntity<Resource> getHomeworkActionResource(@PathVariable(name = "id") Long homeworkActionId) {
        try {
            Resource file = homeworkService.getHomeworkActionResource(homeworkActionId);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
        } catch (HomeworkActionNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Homework Not Found");
        } catch (HomeworkActionNotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Homework Action Not Allowed");
        } catch (NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Action Not Allowed");
        } catch (CourseNotEnabledException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course Not Enabled");
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error");
        }
    }
}

package it.polito.ai.virtuallabs.backend.controllers;

import it.polito.ai.virtuallabs.backend.dtos.HomeworkDTO;
import it.polito.ai.virtuallabs.backend.services.CourseNotFoundException;
import it.polito.ai.virtuallabs.backend.services.HomeworkDueDateException;
import it.polito.ai.virtuallabs.backend.services.HomeworkService;
import it.polito.ai.virtuallabs.backend.services.NotAllowedException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;

@RestController
@RequestMapping("/api/homeworks")
public class HomeworkController {
    @Autowired
    HomeworkService homeworkService;

    @PostMapping({ "", "/" })
    @ResponseStatus(HttpStatus.CREATED)
    public void postHomework(@RequestParam Map<String, Object> input, @RequestParam("file") MultipartFile file) throws CourseNotFoundException, HomeworkDueDateException, RuntimeException, NotAllowedException {
        String courseCode = "";

        if(!input.containsKey("courseCode") || !input.containsKey("homeworkDue")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
        }
        courseCode = (String) input.get("courseCode");

        try {
            homeworkService.storeHomework(file, courseCode, Long.parseLong((String)input.get("homeworkDue")));
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course Not Found");
        }catch (HomeworkDueDateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Due Date is not correct");
        }catch (NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Action Not Allowed");
        }
        catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Sever Error");
        }

    }
}

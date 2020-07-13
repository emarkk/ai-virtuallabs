package it.polito.ai.virtuallabs.backend.controllers;

import it.polito.ai.virtuallabs.backend.dtos.HomeworkDTO;
import it.polito.ai.virtuallabs.backend.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    @Autowired
    FilesStorageService storageService;

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

    @GetMapping("/resource/{courseCode}/{fileName}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable("courseCode") String courseCode, @PathVariable("fileName") String fileName) {
        Resource file = storageService.load("homeworks/" + courseCode + "/" + fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}

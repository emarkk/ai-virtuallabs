package it.polito.ai.virtuallabs.backend.controllers;

import it.polito.ai.virtuallabs.backend.dtos.*;
import it.polito.ai.virtuallabs.backend.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    CourseService courseService;

    @GetMapping("/{code}")
    public CourseDTO getCourse(@PathVariable("code") String courseCode) {
        try{
            return courseService.getCourse(courseCode);
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course '" + courseCode + "' not found");
        } catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        }
    }

    @GetMapping("/{code}/enrolled")
    public PageDTO<CourseStudentDTO> getEnrolledStudents(
            @PathVariable("code") String courseCode,
            @RequestParam(name = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(name = "sortDirection", required = false, defaultValue = "asc") String sortDirection,
            @RequestParam(name = "page", required = false, defaultValue = "0") String page,
            @RequestParam(name = "pageSize", required = false, defaultValue = "15") String pageSize) {
        try {
            return courseService.getEnrolledStudents(courseCode, sortBy, sortDirection, Integer.parseInt(page), Integer.parseInt(pageSize));
        } catch(CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course '" + courseCode + "' not found");
        } catch (InvalidPageException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "page or pageSize Field Not Valid");
        } catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        }
    }

    @GetMapping("/{code}/professors")
    public List<ProfessorDTO> getProfessors(@PathVariable("code") String courseCode) {
        try {
            return courseService.getProfessors(courseCode);
        } catch(CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course '" + courseCode + "' not found");
        } catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        }
    }

    @GetMapping("/{code}/teams")
    public List<TeamDTO> getTeams(@PathVariable("code") String courseCode) {
        try {
            return courseService.getTeams(courseCode);
        } catch(CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course '" + courseCode + "' not found");
        } catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        }
    }

    @GetMapping("/{code}/vm/model")
    public VmModelDTO getVmModel(@PathVariable("code") String courseCode) {
        try {
            return courseService.getVmModel(courseCode);
        } catch(CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course '" + courseCode + "' not found");
        }  catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        }
    }

    @PostMapping({ "", "/" })
    @ResponseStatus(HttpStatus.CREATED)
    public CourseDTO addCourse(@RequestBody CourseDTO courseDTO) {
        boolean result = courseService.addCourse(courseDTO);

        if(!result)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course '" +
                    courseDTO.getCode() + "' already exists");

        return courseDTO;
    }

    @PostMapping("/{code}/enable")
    public void enable(@PathVariable("code") String courseCode) {
        try {
            courseService.enableCourse(courseCode);
        } catch(CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course '" + courseCode + "' not found");
        } catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        }
    }

    @PostMapping("/{code}/disable")
    public void disable(@PathVariable("code") String courseCode) {
        try {
            courseService.disableCourse(courseCode);
        } catch(CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course '" + courseCode + "' not found");
        } catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        }
    }

    @PostMapping("/{code}/enroll")
    public void addStudentToCourse(@PathVariable("code") String courseCode, @RequestBody Map<String, String> input) {
        if(!input.containsKey("id"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");

        Long studentId = Long.parseLong(input.get("id"));
        try {
            if(!courseService.addStudentToCourse(studentId, courseCode))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Student already enrolled to course");
        } catch(CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course '" + courseCode + "' not found");
        } catch(CourseNotEnabledException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course '" + courseCode + "' is not enabled");
        } catch(StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student '" + studentId + "' not found");
        } catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        }
    }

    @PostMapping("/{code}/enroll/csv")
    public List<Boolean> enrollAllViaCsv(@PathVariable("code") String courseCode, @RequestParam("csvFile") MultipartFile csvFile) {
        try {
            String mimeType = mimeType = csvFile.getContentType();
            if(!mimeType.equals("text/csv") && !mimeType.equals("application/vnd.ms-excel")) {
                throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Provided file has unsupported format.");
            }
            return courseService.enrollAllViaCsv(csvFile, courseCode);
        } catch(CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course '" + courseCode + "' not found");
        } catch(CourseNotEnabledException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course '" + courseCode + "' is not enabled");
        } catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        } catch (CsvFileErrorException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while parsing Csv file");
        } catch(StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }
    }

    @PostMapping("/{code}/unenroll")
    public List<Boolean> unenrollStudents(@PathVariable("code") String code, @RequestBody List<Long> ids) throws CourseNotFoundException, StudentNotFoundException, NotAllowedException, CourseNotEnabledException{
        try {
            return courseService.unenrollAll(ids, code);
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, code + ": Course Not Found");
        } catch (StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One Or More Students Not Found");
        } catch (CourseNotEnabledException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course Not Enables");
        } catch (NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized Action");
        }
    }

    @PostMapping("/{code}/unenroll/all")
    public void unenrollAll(@PathVariable(name = "code") String courseCode) {
        try{
            courseService.unenrollAllStudents(courseCode);
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, courseCode + ": Course Not Found");
        } catch (CourseNotEnabledException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course Not Enables");
        } catch (NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized Action");
        }
    }

    @PostMapping("/{code}/professors")
    public void inviteProfessor(@PathVariable("code") String courseCode, @RequestBody Map<String, String> input) {
        if(!input.containsKey("id"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");

        Long professorId = Long.parseLong(input.get("id"));
        try {
            if(!courseService.inviteProfessor(courseCode, professorId))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Professor already managing the course");
        } catch(CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course '" + courseCode + "' not found");
        } catch(ProfessorNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor '" + professorId + "' not found");
        } catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        }
    }

    @PutMapping("/{code}")
    public void updateCourse(@PathVariable("code") String courseCode, @RequestBody CourseDTO courseDTO) {
        try {
            courseService.updateCourse(courseCode, courseDTO);
        } catch(CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course '" + courseCode + "' not found");
        } catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        }
    }

    @DeleteMapping("/{code}")
    public void deleteOne(@PathVariable("code") String courseCode) {
        try {
            courseService.removeCourse(courseCode);
        } catch(CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course '" + courseCode + "' not found");
        } catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        }
    }

    @GetMapping("/{code}/homeworks")
    public List<HomeworkDTO> getHomeworks(@PathVariable("code") String courseCode) {
        try{
            return courseService.getHomeworks(courseCode);
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course " + courseCode + "Not Found");
        } catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        }
    }


}

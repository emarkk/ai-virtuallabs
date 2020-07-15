package it.polito.ai.virtuallabs.backend.controllers;

import it.polito.ai.virtuallabs.backend.dtos.*;
import it.polito.ai.virtuallabs.backend.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    CourseService courseService;

    @GetMapping({ "", "/" })
    public List<CourseDTO> getAll() {
        return courseService.getAllCourses();
    }

    @GetMapping("/{code}")
    public CourseDTO getOne(@PathVariable("code") String courseCode) {
        Optional<CourseDTO> courseOptional = courseService.getCourse(courseCode);

        if(courseOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course '" + courseCode + "' not found");

        return courseOptional.get();
    }

    @GetMapping("/{code}/enrolled")
    public List<StudentDTO> getEnrolledStudents(@PathVariable("code") String courseCode) {
        try {
            return courseService.getEnrolledStudents(courseCode);
        } catch(CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course '" + courseCode + "' not found");
        }
    }

    @GetMapping("/{code}/professors")
    public List<ProfessorDTO> getProfessors(@PathVariable("code") String courseCode) {
        try {
            return courseService.getProfessors(courseCode);
        } catch(CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course '" + courseCode + "' not found");
        }
    }

    @GetMapping("/{code}/teams")
    public List<TeamDTO> getTeams(@PathVariable("code") String courseCode) {
        try {
            return courseService.getTeams(courseCode);
        } catch(CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course '" + courseCode + "' not found");
        }
    }

    @GetMapping("/{code}/enrolled/teamed")
    public List<StudentDTO> getStudentsInTeams(@PathVariable("code") String courseCode) {
        try {
            return courseService.getStudentsInTeams(courseCode);
        } catch(CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course '" + courseCode + "' not found");
        }
    }

    @GetMapping("/{code}/enrolled/unteamed")
    public List<StudentDTO> getStudentsNotInTeams(@PathVariable("code") String courseCode) {
        try {
            return courseService.getStudentsNotInTeams(courseCode);
        } catch(CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course '" + courseCode + "' not found");
        }
    }

    @PostMapping({ "", "/" })
    @ResponseStatus(HttpStatus.CREATED)
    public CourseDTO addOne(@RequestBody CourseDTO courseDTO) {
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
    public void enrollOne(@PathVariable("code") String courseCode, @RequestBody Map<String, String> input) {
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

    @PostMapping("/{code}/professors")
    public void addProfessor(@PathVariable("code") String courseCode, @RequestBody Map<String, String> input) {
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
    public void updateOne(@PathVariable("code") String courseCode, @RequestBody CourseDTO courseDTO) {
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
    public List<HomeworkDTO> getCourseHomeworkData(@PathVariable("code") String courseCode) throws CourseNotFoundException {
        try{
            return courseService.getHomeworksData(courseCode);
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course " + courseCode + "Not Found");
        }
    }


}

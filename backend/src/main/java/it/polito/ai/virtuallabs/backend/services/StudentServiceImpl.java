package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.CourseDTO;
import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamDTO;
import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Student;
import it.polito.ai.virtuallabs.backend.repositories.CourseRepository;
import it.polito.ai.virtuallabs.backend.repositories.StudentRepository;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModelMapper modelMapper;

    private Student _getStudent(Long studentId) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);

        if(studentOptional.isEmpty())
            throw new StudentNotFoundException();

        return studentOptional.get();
    }

    @Override
    public Optional<StudentDTO> getStudent(Long studentId) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        return studentOptional.map(s -> modelMapper.map(s, StudentDTO.class));
    }

    @Override
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseDTO> getCoursesForStudent(Long studentId) {
        return this._getStudent(studentId).getCourses()
                .stream()
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamDTO> getTeamsForStudent(Long studentId) {
        return this._getStudent(studentId).getTeams()
                .stream()
                .map(c -> modelMapper.map(c, TeamDTO.class))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<StudentDTO> getOrderedSearchResult(String q, String exclude) {
        List<Student> retrievedStudents;
        if(exclude != null) {
            Course c = _getCourse(exclude);
            retrievedStudents = studentRepository.getByResumedInfosContainingAndCoursesIsNotContaining(q, c);
        } else {
            retrievedStudents = studentRepository.getByResumedInfosContaining(q);
        }
        return retrievedStudents.stream()
                .map(s -> {
                    HashMap<String, Object> elem = new HashMap<>();
                    elem.put("elem", s);
                    elem.put("distance", StringUtils.getLevenshteinDistance(s.getResumedInfos(), q));
                    return elem;
                })
                .sorted(Comparator.comparingInt(e -> (int) e.get("distance")))
                .limit(3)
                .map(e -> modelMapper.map(e.get("elem"), StudentDTO.class))
                .collect(Collectors.toList());
    }

    private Course _getCourse(String courseCode) {
        Optional<Course> courseOptional = courseRepository.findById(courseCode);

        if(courseOptional.isEmpty())
            throw new CourseNotFoundException();

        return courseOptional.get();
    }
}

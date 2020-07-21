package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.CourseDTO;
import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamDTO;
import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Student;
import it.polito.ai.virtuallabs.backend.repositories.CourseRepository;
import it.polito.ai.virtuallabs.backend.repositories.StudentRepository;
import it.polito.ai.virtuallabs.backend.repositories.specifications.StudentSpecifications;
import it.polito.ai.virtuallabs.backend.utils.UserSearchEngine;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
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

    private Course _getCourse(String courseCode) {
        Optional<Course> courseOptional = courseRepository.findById(courseCode);

        if(courseOptional.isEmpty())
            throw new CourseNotFoundException();

        return courseOptional.get();
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
                .map(t -> modelMapper.map(t, TeamDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> search(String q, String course, Boolean teamed, String excludeCourse, List<Long> excludeIds) {
        Specification<Student> filters = Specification.where(null);

        if(course != null) {
            Course c = _getCourse(course);
            filters = filters.and(StudentSpecifications.enrolledInCourse(c));
            if(teamed != null)
                filters = filters.and(StudentSpecifications.teamedForCourse(c, teamed));
        }
        if(excludeCourse != null)
            filters = filters.and(StudentSpecifications.notEnrolledInCourse(_getCourse(excludeCourse)));
        if(excludeIds != null)
            filters = filters.and(StudentSpecifications.excludeIds(excludeIds));

        return studentRepository.findAll(filters)
                .stream()
                .map(s -> new AbstractMap.SimpleEntry<>(s, UserSearchEngine.getSimilarity(q, "s" + s.getId(), s.getFirstName(), s.getLastName())))
                .sorted(Comparator.comparingDouble(AbstractMap.SimpleEntry<Student, Double>::getValue).reversed())
                .limit(3)
                .map(e -> modelMapper.map(e.getKey(), StudentDTO.class))
                .collect(Collectors.toList());
    }
}

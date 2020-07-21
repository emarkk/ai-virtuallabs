package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.CourseDTO;
import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamDTO;
import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Student;
import it.polito.ai.virtuallabs.backend.repositories.StudentRepository;
import it.polito.ai.virtuallabs.backend.repositories.specifications.StudentSpecifications;
import it.polito.ai.virtuallabs.backend.utils.GetterProxy;
import it.polito.ai.virtuallabs.backend.utils.UserSearchEngine;
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
    private ModelMapper modelMapper;

    @Autowired
    private GetterProxy getter;

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
        return getter.student(studentId).getCourses()
                .stream()
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamDTO> getTeamsForStudent(Long studentId) {
        return getter.student(studentId).getTeams()
                .stream()
                .map(ts -> modelMapper.map(ts.getTeam(), TeamDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamDTO> getTeamsForStudent(Long studentId, String courseCode) {
        Course course = getter.course(courseCode);
        return getter.student(studentId).getTeams()
                .stream()
                .filter(ts -> ts.getTeam().getCourse().equals(course))
                .map(ts -> modelMapper.map(ts.getTeam(), TeamDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> search(String q, String course, Boolean teamed, String excludeCourse, List<Long> excludeIds) {
        Specification<Student> filters = Specification.where(null);

        if(course != null) {
            Course c = getter.course(course);
            filters = filters.and(StudentSpecifications.enrolledInCourse(c));
            if(teamed != null)
                filters = filters.and(StudentSpecifications.teamedForCourse(c, teamed));
        }
        if(excludeCourse != null)
            filters = filters.and(StudentSpecifications.notEnrolledInCourse(getter.course(excludeCourse)));
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

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
    public List<StudentDTO> getOrderedSearchResult(String q, String exclude, String include, List<Long> ids, Boolean teamed) {
        List<Student> retrievedStudents;
        if(exclude != null && include == null) {
            //Ricerca con esclusione corso
            Course c = _getCourse(exclude);
            if(!c.getEnabled())
                throw new CourseNotEnabledException();
            retrievedStudents = studentRepository.getByResumedInfosContainingAndCoursesIsNotContaining(q, c);
        } else if(exclude == null && include != null) {
            Course c = _getCourse(include);
            if(!c.getEnabled())
                throw new CourseNotEnabledException();
            retrievedStudents = studentRepository.getByResumedInfosContainingAndCoursesIsContaining(q, c);
        } else if(exclude != null && include != null) {
            Course c1 = _getCourse(exclude);
            Course c2 = _getCourse(include);
            if(!c1.getEnabled())
                throw new CourseNotEnabledException();
            if(!c2.getEnabled())
                throw new CourseNotEnabledException();
            retrievedStudents = studentRepository.getByResumedInfosContainingAndCoursesIsNotContainingAndCoursesIsContaining(q, c1, c2);
        } else {
            //Ricerca senza filtri
            retrievedStudents = studentRepository.getByResumedInfosContaining(q);
        }
        return retrievedStudents.stream()
                .map(s -> {
                    //Per ogni elemento calcolo la distanza di Levenshtein
                    HashMap<String, Object> elem = new HashMap<>();
                    elem.put("elem", s);
                    elem.put("distance", StringUtils.getLevenshteinDistance(s.getResumedInfos(), q));
                    return elem;
                })
                .sorted(Comparator.comparingInt(e -> (int) e.get("distance")))
                .filter(e -> {
                    //Filtro team
                    if(teamed != null) {
                        Student s = (Student) e.get("elem");
                        boolean teamStatus = !s.getTeams().isEmpty();
                        return teamStatus == teamed;
                    }
                    return true;
                })
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

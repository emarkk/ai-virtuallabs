package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.CourseDTO;
import it.polito.ai.virtuallabs.backend.dtos.ProfessorDTO;
import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Professor;
import it.polito.ai.virtuallabs.backend.entities.Student;
import it.polito.ai.virtuallabs.backend.repositories.CourseRepository;
import it.polito.ai.virtuallabs.backend.repositories.ProfessorRepository;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProfessorServiceImpl implements ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModelMapper modelMapper;

    private Professor _getProfessor(Long professorId) {
        Optional<Professor> professorOptional = professorRepository.findById(professorId);

        if(professorOptional.isEmpty())
            throw new ProfessorNotFoundException();

        return professorOptional.get();
    }

    @Override
    public Optional<ProfessorDTO> getProfessor(Long professorId) {
        Optional<Professor> professorOptional = professorRepository.findById(professorId);
        return professorOptional.map(s -> modelMapper.map(s, ProfessorDTO.class));
    }

    @Override
    public List<ProfessorDTO> getAllProfessors() {
        return professorRepository.findAll()
                .stream()
                .map(s -> modelMapper.map(s, ProfessorDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseDTO> getCoursesForProfessor(Long professorId) {
        return this._getProfessor(professorId).getCourses()
                .stream()
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<ProfessorDTO> getOrderedSearchResult(String q, String exclude) {
        List<Professor> retrievedProfessors;
        if(exclude != null) {
            Course c = _getCourse(exclude);
            retrievedProfessors = professorRepository.getByResumedInfosContainingAndCoursesIsNotContaining(q, c);
        } else {
            retrievedProfessors = professorRepository.getByResumedInfosContaining(q);
        }
        return retrievedProfessors.stream()
                .map(s -> {
                    HashMap<String, Object> elem = new HashMap<>();
                    elem.put("elem", s);
                    elem.put("distance", StringUtils.getLevenshteinDistance(s.getResumedInfos(), q));
                    return elem;
                })
                .sorted(Comparator.comparingInt(e -> (int) e.get("distance")))
                .limit(3)
                .map(e -> modelMapper.map(e.get("elem"), ProfessorDTO.class))
                .collect(Collectors.toList());
    }

    private Course _getCourse(String courseCode) {
        Optional<Course> courseOptional = courseRepository.findById(courseCode);

        if(courseOptional.isEmpty())
            throw new CourseNotFoundException();

        return courseOptional.get();
    }
}

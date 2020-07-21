package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.CourseDTO;
import it.polito.ai.virtuallabs.backend.dtos.ProfessorDTO;
import it.polito.ai.virtuallabs.backend.entities.Professor;
import it.polito.ai.virtuallabs.backend.repositories.ProfessorRepository;
import it.polito.ai.virtuallabs.backend.repositories.specifications.ProfessorSpecifications;
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
public class ProfessorServiceImpl implements ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GetterProxy getter;

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
        return getter.professor(professorId).getCourses()
                .stream()
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProfessorDTO> search(String q, String excludeCourse) {
        Specification<Professor> filters = Specification.where(null);

        if(excludeCourse != null)
            filters = filters.and(ProfessorSpecifications.notTeachingCourse(getter.course(excludeCourse)));

        return professorRepository.findAll(filters)
                .stream()
                .map(p -> new AbstractMap.SimpleEntry<>(p, UserSearchEngine.getSimilarity(q, "d" + p.getId(), p.getFirstName(), p.getLastName())))
                .sorted(Comparator.comparingDouble(AbstractMap.SimpleEntry<Professor, Double>::getValue).reversed())
                .limit(3)
                .map(p -> modelMapper.map(p, ProfessorDTO.class))
                .collect(Collectors.toList());
    }
}

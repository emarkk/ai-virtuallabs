package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.CourseDTO;
import it.polito.ai.virtuallabs.backend.dtos.ProfessorDTO;
import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.entities.Professor;
import it.polito.ai.virtuallabs.backend.entities.Student;
import it.polito.ai.virtuallabs.backend.repositories.ProfessorRepository;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProfessorServiceImpl implements ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

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
    public List<ProfessorDTO> getOrderedSearchResult(String q) {
        Comparator<Professor> distance = new Comparator<Professor>() {
            @Override
            public int compare(Professor o1, Professor o2) {
                int d1 = StringUtils.getLevenshteinDistance(o1.getResumedInfos(), q);
                int d2 = StringUtils.getLevenshteinDistance(o2.getResumedInfos(), q);
                if (d1 == d2) return 0;
                return d1>d2 ? 1 : -1;
            }
        };
        return professorRepository.getByResumedInfosContaining(q).stream()
                .limit(3)
                .sorted(distance)
                .map(s -> modelMapper.map(s, ProfessorDTO.class))
                .collect(Collectors.toList());
    }

}

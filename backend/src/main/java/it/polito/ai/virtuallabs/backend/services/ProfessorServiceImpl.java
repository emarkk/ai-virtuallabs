package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.CourseDTO;
import it.polito.ai.virtuallabs.backend.dtos.ProfessorDTO;
import it.polito.ai.virtuallabs.backend.entities.Professor;
import it.polito.ai.virtuallabs.backend.entities.Student;
import it.polito.ai.virtuallabs.backend.repositories.ProfessorRepository;
import it.polito.ai.virtuallabs.backend.repositories.specifications.ProfessorSpecifications;
import it.polito.ai.virtuallabs.backend.security.AuthenticatedEntityMapper;
import it.polito.ai.virtuallabs.backend.utils.GetterProxy;
import it.polito.ai.virtuallabs.backend.utils.ProfilePicturesUtility;
import it.polito.ai.virtuallabs.backend.utils.UserSearchEngine;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private AuthenticatedEntityMapper authenticatedEntityMapper;

    @Autowired
    private GetterProxy getter;

    @Autowired
    private ProfilePicturesUtility picturesUtility;

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

    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    @Override
    public void addPicture(Long id, MultipartFile file) {
        Professor professor = getter.professor(id);
        if(!((Professor) authenticatedEntityMapper.get()).equals(professor))
            throw new NotAllowedException();
        picturesUtility.postProfilePicture(id, ProfilePicturesUtility.ProfileType.PROFESSOR, file);
        professor.setHasPicture(true);
        professorRepository.save(professor);
    }

    @Override
    public Resource getPicture(Long id) {
        Professor professor = getter.professor(id);
        if(!professor.getHasPicture())
            throw new ProfilePictureNotFoundException();
        return picturesUtility.getProfilePicture(id, ProfilePicturesUtility.ProfileType.PROFESSOR);
    }
}

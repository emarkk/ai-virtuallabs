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
    public ProfessorDTO getProfessor(Long professorId) {
        Professor professor = getter.professor(professorId);

        return modelMapper.map(professor, ProfessorDTO.class);
    }

    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    @Override
    public List<CourseDTO> getCoursesForProfessor(Long professorId) {
        Professor professor = getter.professor(professorId);

        if(!professor.equals((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        return professor.getCourses()
                .stream()
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProfessorDTO> search(String q, String excludeCourse) {
        // initially no filter
        Specification<Professor> filters = Specification.where(null);

        if(excludeCourse != null)
            filters = filters.and(ProfessorSpecifications.notTeachingCourse(getter.course(excludeCourse)));

        return professorRepository.findAll(filters)
                .stream()
                // compute match quality
                .map(p -> new AbstractMap.SimpleEntry<>(p, UserSearchEngine.getSimilarity(q, "d" + p.getId(), p.getFirstName(), p.getLastName())))
                // sort to have best matches first
                .sorted(Comparator.comparingDouble(AbstractMap.SimpleEntry<Professor, Double>::getValue).reversed())
                // limit to three matches
                .limit(3)
                .map(e -> modelMapper.map(e.getKey(), ProfessorDTO.class))
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

package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.CourseDTO;
import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamDTO;
import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Student;
import it.polito.ai.virtuallabs.backend.repositories.StudentRepository;
import it.polito.ai.virtuallabs.backend.repositories.specifications.StudentSpecifications;
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
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthenticatedEntityMapper authenticatedEntityMapper;

    @Autowired
    private GetterProxy getter;

    @Autowired
    private ProfilePicturesUtility picturesUtility;

    @Override
    public StudentDTO getStudent(Long studentId) {
        return modelMapper.map(getter.student(studentId), StudentDTO.class);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Override
    public List<CourseDTO> getCoursesForStudent(Long studentId) {
        Student student = getter.student(studentId);

        if(!student.equals((Student) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        return student.getCourses()
                .stream()
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Override
    public List<TeamDTO> getTeamsForStudent(Long studentId) {
        Student student = getter.student(studentId);

        if(!student.equals((Student) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        return student.getTeams()
                .stream()
                .map(ts -> modelMapper.map(ts.getTeam(), TeamDTO.class))
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Override
    public List<TeamDTO> getTeamsForStudent(Long studentId, String courseCode) {
        Course course = getter.course(courseCode);
        Student student = getter.student(studentId);

        if(!student.equals((Student) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        return student.getTeams()
                .stream()
                .filter(ts -> ts.getTeam().getCourse().equals(course))
                .map(ts -> modelMapper.map(ts.getTeam(), TeamDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> search(String q, String course, Boolean teamed, String excludeCourse, List<Long> excludeIds) {
        // initially no filter
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
                // compute match quality
                .map(s -> new AbstractMap.SimpleEntry<>(s, UserSearchEngine.getSimilarity(q, "s" + s.getId(), s.getFirstName(), s.getLastName())))
                // sort to have best matches first
                .sorted(Comparator.comparingDouble(AbstractMap.SimpleEntry<Student, Double>::getValue).reversed())
                // limit to three matches
                .limit(3)
                .map(e -> modelMapper.map(e.getKey(), StudentDTO.class))
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Override
    public void addPicture(Long id, MultipartFile file) {
        Student student = getter.student(id);

        if(!((Student) authenticatedEntityMapper.get()).equals(student))
            throw new NotAllowedException();

        picturesUtility.postProfilePicture(id, ProfilePicturesUtility.ProfileType.STUDENT, file);
        student.setHasPicture(true);
        studentRepository.save(student);
    }

    @Override
    public Resource getPicture(Long id) {
        Student student = getter.student(id);

        if(!student.getHasPicture())
            throw new ProfilePictureNotFoundException();

        return picturesUtility.getProfilePicture(id, ProfilePicturesUtility.ProfileType.STUDENT);
    }
}

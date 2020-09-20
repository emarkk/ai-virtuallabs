package it.polito.ai.virtuallabs.backend.utils;

import it.polito.ai.virtuallabs.backend.entities.*;
import it.polito.ai.virtuallabs.backend.repositories.*;
import it.polito.ai.virtuallabs.backend.security.AuthenticatedEntityMapper;
import it.polito.ai.virtuallabs.backend.security.jwt.JwtTokenProvider;
import it.polito.ai.virtuallabs.backend.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.util.Optional;

public class GetterProxy {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private HomeworkActionRepository homeworkActionRepository;

    @Autowired
    private VmModelRepository vmModelRepository;

    @Autowired
    private VmRepository vmRepository;

    @Autowired
    private HomeworkRepository homeworkRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticatedEntityMapper authenticatedEntityMapper;

    public Course course(String courseCode) {
        Optional<Course> courseOptional = courseRepository.findById(courseCode);

        if(courseOptional.isEmpty())
            throw new CourseNotFoundException();

        return courseOptional.get();
    }

    public Student student(Long studentId) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);

        if(studentOptional.isEmpty())
            throw new StudentNotFoundException();

        return studentOptional.get();
    }

    public Professor professor(Long professorId) {
        Optional<Professor> professorOptional = professorRepository.findById(professorId);

        if(professorOptional.isEmpty())
            throw new ProfessorNotFoundException();

        return professorOptional.get();
    }

    public Team team(Long teamId) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);

        if(teamOptional.isEmpty())
            throw new TeamNotFoundException();

        return teamOptional.get();
    }

    public VmModel vmModel(Long vmModelId) {
        Optional<VmModel> vmModelOptional = vmModelRepository.findById(vmModelId);

        if(vmModelOptional.isEmpty())
            throw new VmModelNotFoundException();

        return vmModelOptional.get();
    }

    public Vm vm(Long vmId) {
        Optional<Vm> vmOptional = vmRepository.findById(vmId);

        if(vmOptional.isEmpty())
            throw new VmNotFoundException();

        return vmOptional.get();
    }

    public Homework homework(Long homeworkId) {
        Optional<Homework> homeworkOptional = homeworkRepository.findById(homeworkId);

        if(homeworkOptional.isEmpty())
            throw new HomeworkNotFoundException();

        return homeworkOptional.get();
    }

    public HomeworkAction homeworkAction(Long homeworkActionId) {
        Optional<HomeworkAction> homeworkActionOptional = homeworkActionRepository.findById(homeworkActionId);

        if(homeworkActionOptional.isEmpty())
            throw new HomeworkActionNotFoundException();

        return homeworkActionOptional.get();
    }

    public AuthenticatedEntity authenticatedEntity(StompHeaderAccessor accessor) {
        String token = accessor.getFirstNativeHeader("token");
        if(token != null && jwtTokenProvider.validateToken(token))
            return authenticatedEntityMapper.getByAuthentication(jwtTokenProvider.getAuthentication(token));
        return null;
    }

}

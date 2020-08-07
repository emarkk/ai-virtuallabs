package it.polito.ai.virtuallabs.backend.utils;

import it.polito.ai.virtuallabs.backend.entities.*;
import it.polito.ai.virtuallabs.backend.repositories.*;
import it.polito.ai.virtuallabs.backend.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private VmModelRepository vmModelRepository;

    @Autowired
    private VmRepository vmRepository;

    @Autowired
    private HomeworkRepository homeworkRepository;

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

}

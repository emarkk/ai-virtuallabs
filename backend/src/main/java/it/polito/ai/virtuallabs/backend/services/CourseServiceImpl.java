package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.CourseDTO;
import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamDTO;
import it.polito.ai.virtuallabs.backend.entities.AuthenticatedEntity;
import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Professor;
import it.polito.ai.virtuallabs.backend.entities.Student;
import it.polito.ai.virtuallabs.backend.repositories.CourseRepository;
import it.polito.ai.virtuallabs.backend.repositories.ProfessorRepository;
import it.polito.ai.virtuallabs.backend.repositories.StudentRepository;
import it.polito.ai.virtuallabs.backend.security.AuthenticatedEntityMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    ProfessorRepository professorRepository;

    @Autowired
    AuthenticatedEntityMapper authenticatedEntityMapper;

    @Autowired
    ModelMapper modelMapper;

    private Course _getCourse(String courseCode) {
        Optional<Course> courseOptional = courseRepository.findById(courseCode);

        if(courseOptional.isEmpty())
            throw new CourseNotFoundException();

        return courseOptional.get();
    }

    private Student _getStudent(Long studentId) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);

        if(studentOptional.isEmpty())
            throw new StudentNotFoundException();

        return studentOptional.get();
    }

    private Professor _getProfessor(Long professorId) {
        Optional<Professor> professorOptional = professorRepository.findById(professorId);

        if(professorOptional.isEmpty())
            throw new ProfessorNotFoundException();

        return professorOptional.get();
    }

    @Override
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public boolean addCourse(CourseDTO course) {
        
        Course c = modelMapper.map(course, Course.class);


        if(courseRepository.existsById(course.getCode()))
            return false;


        c.addProfessor(((Professor) authenticatedEntityMapper.get()));
        courseRepository.save(c);
        return true;
    }

    @Override
    public Optional<CourseDTO> getCourse(String courseCode) {
        Optional<Course> courseOptional = courseRepository.findById(courseCode);
        return courseOptional.map(c -> modelMapper.map(c, CourseDTO.class));
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public void updateCourse(String courseCode, CourseDTO courseDTO) {
        Course course = this._getCourse(courseCode);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        course.setName(courseDTO.getName());
        course.setAcronym(courseDTO.getAcronym());
        course.setMinTeamMembers(courseDTO.getMinTeamMembers());
        course.setMaxTeamMembers(courseDTO.getMaxTeamMembers());
        course.setEnabled(courseDTO.getEnabled());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public void removeCourse(String courseCode) {
        Course course = this._getCourse(courseCode);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        courseRepository.delete(course);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public boolean inviteProfessor(String courseCode, Long professorId) {
        Course course = this._getCourse(courseCode);
        Professor professor = this._getProfessor(professorId);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        if(course.getProfessors().contains(professor))
            return false;

        course.addProfessor(professor);
        return true;
    }

    @Override
    public List<StudentDTO> getEnrolledStudents(String courseCode) {
        return this._getCourse(courseCode).getStudents()
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamDTO> getTeams(String courseCode) {
        return this._getCourse(courseCode).getTeams()
                .stream()
                .map(t -> modelMapper.map(t, TeamDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getStudentsInTeams(String courseCode) {
        if(!courseRepository.existsById(courseCode))
            throw new CourseNotFoundException();

        return courseRepository.getStudentsInTeams(courseCode)
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getStudentsNotInTeams(String courseCode) {
        if(!courseRepository.existsById(courseCode))
            throw new CourseNotFoundException();

        return courseRepository.getStudentsNotInTeams(courseCode)
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public boolean addStudentToCourse(Long studentId, String courseCode) {
        Student student = this._getStudent(studentId);
        Course course = this._getCourse(courseCode);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        if(!course.getEnabled())
            throw new CourseNotEnabledException();

        if(student.getCourses().contains(course))
            return false;

        course.addStudent(student);
        return true;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public List<Boolean> enrollAll(List<Long> studentsIds, String courseCode) {
        return studentsIds.stream().map(i -> addStudentToCourse(i, courseCode)).collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public void enableCourse(String courseCode) {
        Course course = this._getCourse(courseCode);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        course.setEnabled(true);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public void disableCourse(String courseCode) {
        Course course = this._getCourse(courseCode);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        course.setEnabled(false);
    }

}

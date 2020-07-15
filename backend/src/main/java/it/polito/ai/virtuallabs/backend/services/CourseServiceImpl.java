package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.*;
import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Professor;
import it.polito.ai.virtuallabs.backend.entities.Student;
import it.polito.ai.virtuallabs.backend.repositories.CourseRepository;
import it.polito.ai.virtuallabs.backend.repositories.HomeworkRepository;
import it.polito.ai.virtuallabs.backend.repositories.ProfessorRepository;
import it.polito.ai.virtuallabs.backend.repositories.StudentRepository;
import it.polito.ai.virtuallabs.backend.security.AuthenticatedEntityMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    HomeworkRepository homeworkRepository;

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

        c.addProfessor((Professor) authenticatedEntityMapper.get());
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

        course.removeAllProfessors();
        course.removeAllStudents();
        course.removeAllTeams();

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
    public List<StudentDTO> getEnrolledStudents(String courseCode, String sortField, String sortDirection, int page, int pageSize) {
        Course course = _getCourse(courseCode);
        try {
            Student.class.getDeclaredField(sortField);
        } catch (NoSuchFieldException e) {
            throw new StudentClassFieldNotFoundException();
        }
        if(page<0 || pageSize<0) {
            throw new InvalidPageException();
        }
        Page<Student> studentPage = studentRepository.findAllByCoursesIsContaining(
                PageRequest.of(page, pageSize, Sort.by(
                        sortDirection.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortField
                )),
                course);
        return studentPage.stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

//    @Override
//    public List<StudentDTO> getEnrolledStudents(String courseCode) {
//        return this._getCourse(courseCode).getStudents()
//                .stream()
//                .map(s -> modelMapper.map(s, StudentDTO.class))
//                .collect(Collectors.toList());
//    }

    @Override
    public List<ProfessorDTO> getProfessors(String courseCode) {
        return this._getCourse(courseCode).getProfessors()
                .stream()
                .map(p -> modelMapper.map(p, ProfessorDTO.class))
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

    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    @Override
    public List<Boolean> unenrollAll(List<Long> studentsIds, String courseCode) {
        //Vari controlli su esistenza e validità del corso. Controllo su ownership del Professor
        Course course = _getCourse(courseCode);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        if(!course.getEnabled())
            throw new CourseNotEnabledException();
        //Rimuovo ogni Student iscritto al corso, se esiste
        return studentsIds.stream()
                .map(s -> {
                    Student stud = _getStudent(s);
                    if(!stud.getCourses().contains(course)) {
                        return false;
                    }
                    course.removeStudent(stud);
                    return true;
                })
                .collect(Collectors.toList());
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

    @Override
    public List<HomeworkDTO> getHomeworksData(String courseCode) {
        Course course = this._getCourse(courseCode);
            return homeworkRepository.findAllByCourse(course)
                    .stream()
                    .map(h -> modelMapper.map(h, HomeworkDTO.class))
                    .collect(Collectors.toList());
    }

}

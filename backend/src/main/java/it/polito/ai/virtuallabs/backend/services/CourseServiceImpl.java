package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.*;
import it.polito.ai.virtuallabs.backend.entities.*;
import it.polito.ai.virtuallabs.backend.repositories.CourseRepository;
import it.polito.ai.virtuallabs.backend.repositories.HomeworkRepository;
import it.polito.ai.virtuallabs.backend.repositories.StudentRepository;
import it.polito.ai.virtuallabs.backend.security.AuthenticatedEntityMapper;
import it.polito.ai.virtuallabs.backend.utils.GetterProxy;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private HomeworkRepository homeworkRepository;

    @Autowired
    private AuthenticatedEntityMapper authenticatedEntityMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GetterProxy getter;

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
        Course course = getter.course(courseCode);

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
        Course course = getter.course(courseCode);

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
        Course course = getter.course(courseCode);
        Professor professor = getter.professor(professorId);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        if(course.getProfessors().contains(professor))
            return false;

        course.addProfessor(professor);
        return true;
    }

    @Override
    public PageDTO<CourseStudentDTO> getEnrolledStudents(String courseCode, String sortField, String sortDirection, int page, int pageSize) {
        Course course = getter.course(courseCode);
        try {
            Student.class.getDeclaredField(sortField);
        } catch (NoSuchFieldException e) {
            throw new StudentClassFieldNotFoundException();
        }

        if(page < 0 || pageSize < 0)
            throw new InvalidPageException();

        Page<Student> studentPage = studentRepository.findAllByCoursesIsContaining(
                PageRequest.of(page, pageSize, Sort.by(
                        sortDirection.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortField
                )),
                course);

        List<CourseStudentDTO> students = studentPage.stream()
                .map(s -> {
                    Optional<Team> team = s.getTeams().stream().map(TeamStudent::getTeam).filter(t -> t.isComplete() && t.getCourse().equals(course)).findFirst();
                    if(team.isPresent())
                        return new CourseStudentDTO(modelMapper.map(s, StudentDTO.class), modelMapper.map(team.get(), TeamDTO.class));
                    Optional<TeamStudent> ts = s.getTeams().stream().filter(t -> (t.getTeam().isActive()) && (t.getInvitationStatus().equals(TeamStudent.InvitationStatus.ACCEPTED) || t.getInvitationStatus().equals(TeamStudent.InvitationStatus.CREATOR))).findFirst();
                    return ts.map(teamStudent -> new CourseStudentDTO(modelMapper.map(s, StudentDTO.class), modelMapper.map(teamStudent.getTeam(), TeamDTO.class))).orElseGet(() -> new CourseStudentDTO(modelMapper.map(s, StudentDTO.class), null));
                })
                .collect(Collectors.toList());

        return new PageDTO<>(course.getStudents().size(), students);
    }

    @Override
    public List<ProfessorDTO> getProfessors(String courseCode) {
        return getter.course(courseCode).getProfessors()
                .stream()
                .map(p -> modelMapper.map(p, ProfessorDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamDTO> getTeams(String courseCode) {
        return getter.course(courseCode).getTeams()
                .stream()
                .map(t -> modelMapper.map(t, TeamDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public VmModelDTO getVmModel(String courseCode) {
        VmModel vmModel = getter.course(courseCode).getVmModel();

        if(vmModel == null)
            return null;

        return new VmModelDTO(vmModel.getId(), vmModel.getName(), null);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public boolean addStudentToCourse(Long studentId, String courseCode) {
        Student student = getter.student(studentId);
        Course course = getter.course(courseCode);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        if(!course.getEnabled())
            throw new CourseNotEnabledException();

        if(student.getCourses().contains(course))
            return false;

        course.addStudent(student);
        return true;
    }

    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    @Override
    public List<Boolean> enrollAllViaCsv(MultipartFile csvFile, String courseCode) {
        Course course = getter.course(courseCode);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        if(!course.getEnabled())
            throw new CourseNotEnabledException();

        try {
            BufferedReader csvReader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()));
            List<Boolean> result = csvReader.lines()
                    .map(line -> {
                        Long studentId = Long.parseLong(line);
                        Student student = getter.student(studentId);
                        if(student.getCourses().contains(course))
                            return false;
                        course.addStudent(student);
                        return true;
                    })
                    .collect(Collectors.toList());
            csvReader.close();
            return result;
        } catch (IOException | NumberFormatException e) {
            throw new CsvFileErrorException();
        }


    }

    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    @Override
    public List<Boolean> unenrollAll(List<Long> studentsIds, String courseCode) {
        //Vari controlli su esistenza e validità del corso. Controllo su ownership del Professor
        Course course = getter.course(courseCode);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        if(!course.getEnabled())
            throw new CourseNotEnabledException();
        //Rimuovo ogni Student iscritto al corso, se esiste
        return studentsIds.stream()
                .map(s -> {
                    Student stud = getter.student(s);
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
        Course course = getter.course(courseCode);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        course.setEnabled(true);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public void disableCourse(String courseCode) {
        Course course = getter.course(courseCode);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        course.setEnabled(false);
    }

    @Override
    public List<HomeworkDTO> getHomeworksData(String courseCode) {
        Course course = getter.course(courseCode);
            return homeworkRepository.findAllByCourse(course)
                    .stream()
                    .map(h -> modelMapper.map(h, HomeworkDTO.class))
                    .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public void unenrollAllStudents(String courseCode) {
        Course course = getter.course(courseCode);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();
        if(!course.getEnabled())
            throw new CourseNotEnabledException();

        course.removeAllStudents();
    }

    @Override
    public Boolean professorHasSignalPermission(String courseCode, Long professorId) {
        Course course = getter.course(courseCode);
        Professor professor = getter.professor(professorId);

        return course.getProfessors().contains(professor);
    }

}

package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamProposalDTO;
import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Student;
import it.polito.ai.virtuallabs.backend.entities.Team;
import it.polito.ai.virtuallabs.backend.entities.TeamStudent;
import it.polito.ai.virtuallabs.backend.repositories.CourseRepository;
import it.polito.ai.virtuallabs.backend.repositories.StudentRepository;
import it.polito.ai.virtuallabs.backend.repositories.TeamStudentRepository;
import it.polito.ai.virtuallabs.backend.repositories.TeamRepository;
import it.polito.ai.virtuallabs.backend.security.AuthenticatedEntityMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeamStudentRepository teamStudentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthenticatedEntityMapper authenticatedEntityMapper;

    private Team _getTeam(Long teamId) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);

        if(teamOptional.isEmpty())
            throw new TeamNotFoundException();

        return teamOptional.get();
    }

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

    @Override
    public Optional<TeamDTO> getTeam(Long teamId) {
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        return teamOpt.map(t -> modelMapper.map(t, TeamDTO.class));
    }

    @Override
    public List<StudentDTO> getMembers(Long teamId) {
        /*return this._getTeam(teamId).getTeamStudents()
                .stream()
                .map(ts -> modelMapper.map(ts.getStudent(), StudentDTO.class))
                .collect(Collectors.toList());*/
        return null;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TeamDTO proposeTeam(TeamProposalDTO teamProposalDTO) {
        Course course = _getCourse(teamProposalDTO.getCourseCode());
        Student authenticated = (Student) authenticatedEntityMapper.get();

        if(!course.getEnabled())
            throw new CourseNotEnabledException();
        if(!authenticated.getCourses().contains(course))
            throw new StudentNotEnrolledException();
        if(course.getTeams().stream().anyMatch(t -> t.getName().equals(teamProposalDTO.getName())))
            throw new DuplicateTeamNameException();
        if(new HashSet<>(teamProposalDTO.getMembersIds()).size() < teamProposalDTO.getMembersIds().size())
            throw new DuplicateParticipantException();
        if(!teamProposalDTO.getMembersIds().contains(authenticated.getId()))
            throw new IllegalTeamProposalException();

        List<Student> students = teamProposalDTO.getMembersIds()
                .stream()
                .map(this::_getStudent)
                .collect(Collectors.toList());

        if(students.stream().anyMatch(s -> !s.getCourses().contains(course)))
            throw new StudentNotEnrolledException();
        if(students.size() < course.getMinTeamMembers() || students.size() > course.getMaxTeamMembers())
            throw new IllegalTeamSizeException();
        if(students.stream().anyMatch(s ->
                s.getTeams().stream().anyMatch(ts -> ts.getTeam().getFormationStatus() == Team.FormationStatus.COMPLETE)))
            throw new StudentAlreadyInTeamException();

        Team team = Team.builder()
                .name(teamProposalDTO.getName())
                .formationStatus(students.size() > 1 ? Team.FormationStatus.PROVISIONAL : Team.FormationStatus.COMPLETE)
                .invitationsExpiration(new Timestamp(System.currentTimeMillis() + 24*60*60*1000 * teamProposalDTO.getTimeout()))
                .course(course)
                .build();
        teamRepository.save(team);

        List<TeamStudent> members = students.stream().map(s -> new TeamStudent(s, team, s.equals(authenticated)
                    ? TeamStudent.InvitationStatus.CREATOR
                    : TeamStudent.InvitationStatus.PENDING
        )).collect(Collectors.toList());
        members.forEach(m -> teamStudentRepository.save(m));

        return modelMapper.map(team, TeamDTO.class);
    }

    @Override
    public void activateTeam(Long teamId) {
    }

    @Override
    public void evictTeam(Long teamId) {
    }

}

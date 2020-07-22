package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamMemberStatusDTO;
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
import it.polito.ai.virtuallabs.backend.utils.GetterProxy;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamStudentRepository teamStudentRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthenticatedEntityMapper authenticatedEntityMapper;

    @Autowired
    private GetterProxy getter;

    @Override
    public Optional<TeamDTO> getTeam(Long teamId) {
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        return teamOpt.map(t -> modelMapper.map(t, TeamDTO.class));
    }

    @Override
    public List<StudentDTO> getMembers(Long teamId) {
        return getter.team(teamId).getMembers()
                .stream()
                .map(ts -> modelMapper.map(ts.getStudent(), StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamMemberStatusDTO> getMembersStatus(Long teamId) {
        return getter.team(teamId).getMembers()
                .stream()
                .map(ts -> new TeamMemberStatusDTO(modelMapper.map(ts.getStudent(), StudentDTO.class), ts.getInvitationStatus()))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TeamDTO proposeTeam(TeamProposalDTO teamProposalDTO) {
        Course course = getter.course(teamProposalDTO.getCourseCode());
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
                .map(getter::student)
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

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Override
    public void acceptTeam(Long teamId) {
        Team team = getter.team(teamId);
        Student authenticated = (Student) authenticatedEntityMapper.get();

        Optional<TeamStudent> optionalTeamStudent = team.getMembers().stream().filter(ts -> ts.getStudent().equals(authenticated)).findFirst();
        if(optionalTeamStudent.isEmpty()) {
            throw new StudentNotInTeamException();
        }
        TeamStudent ts = optionalTeamStudent.get();
        if(ts.getInvitationStatus().equals(TeamStudent.InvitationStatus.REJECTED) || ts.getInvitationStatus().equals(TeamStudent.InvitationStatus.CREATOR)) {
            throw new IllegalTeamAcceptationException();
        }
        ts.setInvitationStatus(TeamStudent.InvitationStatus.ACCEPTED);
        teamStudentRepository.save(ts);

        List<TeamStudent> updatedInvitations = teamStudentRepository.findAllByTeamId(teamId);

        //Se non ci sono inviti rejected o pending abilito il team
        if (updatedInvitations.stream().noneMatch(up -> up.getInvitationStatus().equals(TeamStudent.InvitationStatus.PENDING)
                || up.getInvitationStatus().equals(TeamStudent.InvitationStatus.REJECTED))) {
            team.setFormationStatus(Team.FormationStatus.COMPLETE);
            teamRepository.save(team);
        }

    }

    @Override
    public void activateTeam(Long teamId) {
    }

    @Override
    public void evictTeam(Long teamId) {
    }


}

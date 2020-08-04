package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.*;
import it.polito.ai.virtuallabs.backend.entities.*;
import it.polito.ai.virtuallabs.backend.repositories.TeamStudentRepository;
import it.polito.ai.virtuallabs.backend.repositories.TeamRepository;
import it.polito.ai.virtuallabs.backend.repositories.VmConfigurationLimitsRepository;
import it.polito.ai.virtuallabs.backend.security.AuthenticatedEntityMapper;
import it.polito.ai.virtuallabs.backend.utils.GetterProxy;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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
    private TeamRepository teamRepository;

    @Autowired
    private TeamStudentRepository teamStudentRepository;

    @Autowired
    private VmConfigurationLimitsRepository vmConfigurationLimitsRepository;

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
    public List<VmDTO> getVms(Long teamId) {
        return getter.team(teamId).getVms()
                .stream()
                .map(vm -> modelMapper.map(vm, VmDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public VmConfigurationLimitsDTO getVmConfigurationLimits(Long teamId) {
        Team team = getter.team(teamId);
        AuthenticatedEntity authenticatedEntity = authenticatedEntityMapper.get();
        if(authenticatedEntity.getClass().equals(Professor.class) && !((Professor) authenticatedEntity).getCourses().contains(team.getCourse()))
            throw new NotAllowedException();
        if(authenticatedEntity.getClass().equals(Student.class) && !((Student) authenticatedEntity).getTeams().stream().map(TeamStudent::getTeam).collect(Collectors.toList()).contains(team))
            throw new StudentNotInTeamException();
        if(team.getVmConfigurationLimits() == null)
            throw new VmConfigurationLimitsNotFoundException();
        return modelMapper.map(team.getVmConfigurationLimits(), VmConfigurationLimitsDTO.class);
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
                s.getTeams().stream().anyMatch(ts -> ts.getTeam().isComplete())))
            throw new StudentAlreadyInTeamException();

        Team team = Team.builder()
                .name(teamProposalDTO.getName())
                .formationStatus(students.size() > 1 ? Team.FormationStatus.PROVISIONAL : Team.FormationStatus.COMPLETE)
                .invitationsExpiration(new Timestamp(System.currentTimeMillis() + 24*60*60*1000 * teamProposalDTO.getTimeout()))
                .lastAction(new Timestamp(System.currentTimeMillis()))
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

        if(!team.isProvisional())
            throw new IllegalTeamInvitationReplyException();

        if(authenticated.getTeams()
                .stream()
                .filter(ts -> ts.getInvitationStatus().equals(TeamStudent.InvitationStatus.ACCEPTED))
                .map(TeamStudent::getTeam)
                .anyMatch(t -> t.isActive())) {
            throw new IllegalTeamInvitationReplyException();
        }

        Optional<TeamStudent> optionalTeamStudent = team.getMembers().stream().filter(ts -> ts.getStudent().equals(authenticated)).findFirst();
        if(optionalTeamStudent.isEmpty())
            throw new StudentNotInTeamException();

        TeamStudent ts = optionalTeamStudent.get();
        if(!ts.getInvitationStatus().equals(TeamStudent.InvitationStatus.PENDING))
            throw new IllegalTeamInvitationReplyException();

        ts.setInvitationStatus(TeamStudent.InvitationStatus.ACCEPTED);
        teamStudentRepository.save(ts);

        //Se non ci sono inviti rejected o pending abilito il team
        if(team.getMembers().stream().noneMatch(m -> m.getInvitationStatus().equals(TeamStudent.InvitationStatus.PENDING))) {
            team.setFormationStatus(Team.FormationStatus.COMPLETE);
            team.setLastAction(new Timestamp(System.currentTimeMillis()));
            teamRepository.save(team);
        }
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Override
    public void declineTeam(Long teamId) {
        Team team = getter.team(teamId);
        Student authenticated = (Student) authenticatedEntityMapper.get();

        if(!team.isProvisional())
            throw new IllegalTeamInvitationReplyException();

        Optional<TeamStudent> optionalTeamStudent = team.getMembers().stream().filter(ts -> ts.getStudent().equals(authenticated)).findFirst();
        if(optionalTeamStudent.isEmpty())
            throw new StudentNotInTeamException();

        TeamStudent ts = optionalTeamStudent.get();
        if(!ts.getInvitationStatus().equals(TeamStudent.InvitationStatus.PENDING))
            throw new IllegalTeamInvitationReplyException();

        ts.setInvitationStatus(TeamStudent.InvitationStatus.REJECTED);
        teamStudentRepository.save(ts);

        //Setto il team come ABORTED
        team.setFormationStatus(Team.FormationStatus.ABORTED);
        team.setLastAction(new Timestamp(System.currentTimeMillis()));
        teamRepository.save(team);
    }

    @Override
    public Boolean studentHasSignalPermission(Long teamId, Long studentId) {
        Team team = getter.team(teamId);
        Student student = getter.student(studentId);

        return team.getMembers().stream().anyMatch(ts -> ts.getStudent() == student);
    }

    @Override
    public Boolean professorHasSignalPermission(Long teamId, Long professorId) {
        Team team = getter.team(teamId);
        Professor professor = getter.professor(professorId);

        return team.getCourse().getProfessors().contains(professor);
    }

    @Scheduled(initialDelay = 2000, fixedRate = 1000 * 60 * 60 * 24)
    public void scheduledExpiredUserClean() {
        long nowMilliseconds = System.currentTimeMillis();
        Timestamp oneWeekAgo = new Timestamp( nowMilliseconds - 1000 * 60 * 60 * 24 * 7);
        Timestamp now = new Timestamp(nowMilliseconds);

        //Passo da PROVISIONAL ad EXPIRED se scaduti da meno di 7 giorni
        List<Team> teams = teamRepository
                .findAllByFormationStatusIsAndInvitationsExpirationIsBetween(
                        Team.FormationStatus.PROVISIONAL, oneWeekAgo, now);
        teams.forEach(et -> {
            et.setFormationStatus(Team.FormationStatus.EXPIRED);
            teamRepository.save(et);
        });

        //Cancello i team EXPIRED da più di 7 giorni
        teams = teamRepository
                .findAllByFormationStatusIsAndInvitationsExpirationIsBefore(
                        Team.FormationStatus.EXPIRED, oneWeekAgo);
        teams.forEach(t -> teamStudentRepository.deleteAll(t.getMembers()));
        teamRepository.deleteAll(teams);

        //Cancello i team ABORTED con lastAction a più di 7 giorni fa
        teams = teamRepository.findAllByFormationStatusIsAndLastActionIsBefore(
                Team.FormationStatus.ABORTED, oneWeekAgo);
        teams.forEach(t -> teamStudentRepository.deleteAll(t.getMembers()));
        teamRepository.deleteAll(teams);
    }
}

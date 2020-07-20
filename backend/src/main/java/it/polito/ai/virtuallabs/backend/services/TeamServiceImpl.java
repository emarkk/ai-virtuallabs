package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamProposalDTO;
import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Student;
import it.polito.ai.virtuallabs.backend.entities.Team;
import it.polito.ai.virtuallabs.backend.entities.TeamInvitation;
import it.polito.ai.virtuallabs.backend.repositories.CourseRepository;
import it.polito.ai.virtuallabs.backend.repositories.StudentRepository;
import it.polito.ai.virtuallabs.backend.repositories.TeamInvitationRepository;
import it.polito.ai.virtuallabs.backend.repositories.TeamRepository;
import it.polito.ai.virtuallabs.backend.security.AuthenticatedEntityMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
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
    TeamInvitationRepository teamInvitationRepository;

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

    @Override
    public Optional<TeamDTO> getTeam(Long teamId) {
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        return teamOpt.map(t -> modelMapper.map(t, TeamDTO.class));
    }

    @Override
    public List<StudentDTO> getMembers(Long teamId) {
        return this._getTeam(teamId).getMembers()
                .stream()
                .map(m -> modelMapper.map(m, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TeamDTO proposeTeam(TeamProposalDTO teamProposalDTO) {

        Student authenticated = (Student) authenticatedEntityMapper.get();

        Course course = _getCourse(teamProposalDTO.getCourseCode());
        if(!course.getEnabled())
            throw new CourseNotEnabledException();
        if(!authenticated.getCourses().contains(course))
            throw new StudentNotEnrolledException();
        if(teamRepository.findByName(teamProposalDTO.getName()) != null) {
            throw new DuplicateTeamNameException();
        }
        int size = (int) teamProposalDTO.getMembersIds().stream().distinct().count();
        if( size > course.getMaxTeamMembers() || size < course.getMinTeamMembers()) {
            throw new IllegalTeamSizeException();
        }

        if(size < teamProposalDTO.getMembersIds().size()) {
            throw new DuplicateParticipantException();
        }

        if(!teamProposalDTO.getMembersIds().contains(authenticated.getId())) {
            throw new IllegalTeamProposalException();
        }

        Team team = new Team();

        team.setName(teamProposalDTO.getName());
        team.setStatus(0);
        team.setCourse(course);

        teamProposalDTO.getMembersIds().forEach(id -> {
            Optional<Student> student = studentRepository.findById(id);
            if(student.isEmpty()) {
                throw new StudentNotFoundException();
            }
            if(!student.get().getCourses().contains(course)) {
                throw new StudentNotEnrolledException();
            }
            if(teamRepository.getTeamByCourseAndMembersAndStatus(course, student.get(), 1) != null) {
                throw new StudentAlreadyInTeamException();
            }

            team.addMember(student.get());

            teamRepository.save(team);

            TeamInvitation teamInvitation = TeamInvitation.builder()
                    .team(team)
                    .expirationDate(new Timestamp(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * teamProposalDTO.getTimeout()))
                    .addresseeStudent(student.get())
                    .status(student.get().getId().equals(authenticated.getId()) ? "creator" : "pending")
                    .build();

            teamInvitationRepository.save(teamInvitation);

        });


        return modelMapper.map(team, TeamDTO.class);
    }


    @Override
    public void activateTeam(Long teamId) {
        Team team = this._getTeam(teamId);
        team.setStatus(1);
        teamRepository.save(team);
    }

    @Override
    public void evictTeam(Long teamId) {
        Team team = this._getTeam(teamId);
        teamRepository.delete(team);
    }

}

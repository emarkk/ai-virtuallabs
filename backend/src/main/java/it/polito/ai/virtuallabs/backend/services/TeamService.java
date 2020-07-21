package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamProposalDTO;
import it.polito.ai.virtuallabs.backend.entities.TeamStudent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TeamService {
    Optional<TeamDTO> getTeam(Long teamId);
    List<StudentDTO> getMembers(Long teamId);
    Map<StudentDTO, TeamStudent.InvitationStatus> getMembersStatus(Long teamId);
    TeamDTO proposeTeam(TeamProposalDTO teamProposalDTO);
    void activateTeam(Long teamId);
    void evictTeam(Long teamId);
}

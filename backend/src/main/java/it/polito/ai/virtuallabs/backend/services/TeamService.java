package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.*;
import it.polito.ai.virtuallabs.backend.entities.TeamStudent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TeamService {
    Optional<TeamDTO> getTeam(Long teamId);
    List<StudentDTO> getMembers(Long teamId);
    List<TeamMemberStatusDTO> getMembersStatus(Long teamId);
    List<VmDTO> getVms(Long teamId);
    TeamDTO proposeTeam(TeamProposalDTO teamProposalDTO);
    void acceptTeam(Long teamId);
    void declineTeam(Long teamId);
    VmConfigurationLimitsDTO getVmConfigurationLimits(Long teamId);
}

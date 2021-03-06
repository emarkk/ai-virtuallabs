package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.*;
import it.polito.ai.virtuallabs.backend.entities.TeamStudent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TeamService {
    List<TeamMemberStatusDTO> getMembersStatus(Long teamId);
    List<VmDTO> getVms(Long teamId);
    TeamVmsResourcesDTO getTeamVmsResourcesUsed(Long teamId);
    TeamVmsResourcesDTO getTeamVmsResourcesLimits(Long teamId);
    TeamDTO proposeTeam(TeamProposalDTO teamProposalDTO);
    void acceptTeam(Long teamId);
    void declineTeam(Long teamId);
    void setTeamVmsResourcesLimits(Long teamId, TeamVmsResourcesDTO limits);
    Boolean studentHasSignalPermission(Long teamId, Long studentId);
    Boolean professorHasSignalPermission(Long teamId, Long professorId);
}

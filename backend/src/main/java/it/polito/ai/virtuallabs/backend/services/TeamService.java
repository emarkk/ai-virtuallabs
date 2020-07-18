package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamDTO;

import java.util.List;
import java.util.Optional;

public interface TeamService {
    Optional<TeamDTO> getTeam(Long teamId);
    List<StudentDTO> getMembers(Long teamId);
    TeamDTO proposeTeam(String courseName, String teamName, List<Long> memberIds, Integer timeout);
    void activateTeam(Long teamId);
    void evictTeam(Long teamId);
}

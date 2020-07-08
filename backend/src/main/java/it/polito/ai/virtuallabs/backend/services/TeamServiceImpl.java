package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamDTO;
import it.polito.ai.virtuallabs.backend.entities.Team;
import it.polito.ai.virtuallabs.backend.repositories.TeamRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    ModelMapper modelMapper;

    private Team _getTeam(Long teamId) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);

        if(teamOptional.isEmpty())
            throw new TeamNotFoundException();

        return teamOptional.get();
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
    public TeamDTO proposeTeam(String courseCode, String teamName, List<String> memberIds) {
        return null;
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

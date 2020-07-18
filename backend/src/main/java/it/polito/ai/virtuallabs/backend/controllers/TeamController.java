package it.polito.ai.virtuallabs.backend.controllers;

import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamDTO;
import it.polito.ai.virtuallabs.backend.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    TeamService teamService;

    @GetMapping("/{id}")
    public TeamDTO getOne(@PathVariable("id") Long id) {
        Optional<TeamDTO> team = teamService.getTeam(id);

        if(team.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team '" + id + "' not found");

        return team.get();
    }

    @GetMapping("/{id}/members")
    public List<StudentDTO> getMembers(@PathVariable("id") Long id) {
        try {
            return teamService.getMembers(id);
        } catch(TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team '" + id + "' not found");
        }
    }

    @PostMapping({ "", "/" })
    @ResponseStatus(HttpStatus.CREATED)
    public void proposeTeam(@RequestBody Map<String, Object> input) {
        String courseCode = "";

        if(!input.containsKey("courseCode") || !input.containsKey("name") || !input.containsKey("membersIds") || !input.containsKey("timeout"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");

        try {
            courseCode = (String) input.get("courseCode");
            String teamName = (String) input.get("name");
            List<Long> members = (List<Long>) input.get("membersIds");
            Integer timeout = (Integer) input.get("timeout");

            TeamDTO team = teamService.proposeTeam(courseCode, teamName, members, timeout);
        } catch(CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course '" + courseCode + "' not found");
        } catch(StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Some students do not exist");
        } catch(CourseNotEnabledException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course '" + courseCode + "' is not enabled");
        } catch(DuplicateParticipantException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicated participants in members list");
        } catch(StudentNotEnrolledException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some students are not enrolled in course '" + courseCode + "'");
        } catch(StudentAlreadyInTeamException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some students already joined a team for course '" + courseCode + "'");
        } catch(IllegalTeamSizeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal team size");
        } catch(IllegalTeamProposalException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Members list does not include requesting user");
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
        }
    }

}

package it.polito.ai.virtuallabs.backend.controllers;

import it.polito.ai.virtuallabs.backend.dtos.*;
import it.polito.ai.virtuallabs.backend.entities.TeamStudent;
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

    @Autowired
    NotificationService notificationService;

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

    @GetMapping("/{id}/members/status")
    public List<TeamMemberStatusDTO> getMembersStatus(@PathVariable("id") Long id) {
        try {
            return teamService.getMembersStatus(id);
        } catch(TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team '" + id + "' not found");
        }
    }

    @GetMapping("/{id}/vms")
    public List<VmDTO> getVms(@PathVariable("id") Long id) {
        try {
            return teamService.getVms(id);
        } catch(TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team '" + id + "' not found");
        }
    }

    @GetMapping("/{id}/vms/resources/used")
    public TeamVmsResourcesDTO getTeamVmsResourcesUsed(@PathVariable(name = "id") Long teamId) {
        try{
            return teamService.getTeamVmsResourcesUsed(teamId);
        } catch(TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team '" + teamId + "' not found");
        } catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        }
    }

    @GetMapping("/{id}/vms/resources/limits")
    public TeamVmsResourcesDTO getTeamVmsResourcesLimits(@PathVariable(name = "id") Long teamId) {
        try{
            return teamService.getTeamVmsResourcesLimits(teamId);
        } catch(TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team '" + teamId + "' not found");
        } catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        }
    }

    @PostMapping({ "", "/" })
    @ResponseStatus(HttpStatus.CREATED)
    public void proposeTeam(@RequestBody TeamProposalDTO teamProposalDTO) {
        try {
            teamService.proposeTeam(teamProposalDTO);
            notificationService.notifyNewGroupProposal(teamProposalDTO);
        } catch(CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course '" + teamProposalDTO.getCourseCode() + "' not found");
        } catch(StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Some students do not exist");
        } catch(CourseNotEnabledException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course '" + teamProposalDTO.getCourseCode() + "' is not enabled");
        } catch(DuplicateParticipantException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicated participants in members list");
        } catch(StudentNotEnrolledException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some students are not enrolled in course '" + teamProposalDTO.getCourseCode() + "'");
        } catch(StudentAlreadyInTeamException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some students already joined a team for course '" + teamProposalDTO.getCourseCode() + "'");
        } catch(IllegalTeamSizeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal team size");
        } catch(IllegalTeamProposalException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Members list does not include requesting user");
        } catch (DuplicateTeamNameException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate Team Name");
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
        }
    }

    @PostMapping("/{id}/accept")
    public void acceptTeam(@PathVariable(name = "id") Long teamId) {
        try {
            teamService.acceptTeam(teamId);
        } catch (TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team Not Found");
        } catch (StudentNotInTeamException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student not enrolled in team");
        } catch (IllegalTeamInvitationReplyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal team invitation accept request");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
        }
    }

    @PostMapping("/{id}/decline")
    public void declineTeam(@PathVariable(name = "id") Long teamId) {
        try {
            teamService.declineTeam(teamId);
        } catch (TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team Not Found");
        } catch (StudentNotInTeamException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student not enrolled in team");
        } catch (IllegalTeamInvitationReplyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal team invitation decline request");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
        }
    }

    @PostMapping("/{id}/vms/resources/limits")
    public void setTeamVmsResourcesLimits(@PathVariable(name = "id") Long teamId, @RequestBody TeamVmsResourcesDTO limits) {
        try{
            teamService.setTeamVmsResourcesLimits(teamId, limits);
        } catch(TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team '" + teamId + "' not found");
        } catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        } catch(IllegalTeamVmsResourcesLimitsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team VMs resources limits must be higher than used resources");
        }
    }

}

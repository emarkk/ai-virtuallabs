package it.polito.ai.virtuallabs.backend.controllers;

import it.polito.ai.virtuallabs.backend.dtos.VmDTO;
import it.polito.ai.virtuallabs.backend.dtos.VmModelDTO;
import it.polito.ai.virtuallabs.backend.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vms")
public class VmController {

    @Autowired
    VmService vmService;

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public VmDTO addVm(@RequestBody Map<String, String> input) {
        if(!input.containsKey("teamId") || !input.containsKey("vcpus") || !input.containsKey("diskSpace") || !input.containsKey("ram")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
        }
        try{
            return vmService.addVm(Long.parseLong(input.get("teamId")), Integer.parseInt(input.get("vcpus")), Integer.parseInt(input.get("diskSpace")), Integer.parseInt(input.get("ram")));
        } catch (TeamNotActiveException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team is not active");
        } catch (StudentNotInTeamException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student not in team");
        } catch (TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team Not Found");
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
        } catch (TeamVmsResourcesLimitsExceededException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team VM limits exceeded");
        }
    }

    @GetMapping("/{id}")
    public VmDTO getVm(@PathVariable(name = "id") Long vmId) {
        try{
            return vmService.getVm(vmId);
        } catch (VmNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vm with id: " + vmId + " not found");
        } catch (StudentNotInTeamException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student not in team");
        } catch (TeamNotActiveException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team is not active");
        } catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        }
    }

    @PutMapping("/{id}")
    public VmDTO updateVm(@PathVariable(name = "id") Long vmId, @RequestBody Map<String, Integer> input) {
        try{
            if(!input.containsKey("diskSpace") || !input.containsKey("ram") || !input.containsKey("vcpus"))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
            return vmService.updateVm(vmId, input.get("vcpus"), input.get("diskSpace"), input.get("ram"));
        } catch (VmNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vm with id: " + vmId + " not found");
        } catch (StudentNotInTeamException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student not in team");
        } catch (TeamVmsResourcesLimitsExceededException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team VM limits exceeded");
        }
    }

    @PatchMapping("/{id}/owners")
    public List<Boolean> addVmOwners(@PathVariable(name = "id") Long vmId, @RequestBody List<Long> studentIds) {
        try{
           return vmService.addVmOwners(vmId, studentIds);
        } catch (VmNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vm with id: " + vmId + " not found");
        } catch (NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        } catch (StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student Not Found");
        } catch (DuplicateParticipantException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate student in list");
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
        } catch (VmOnlineException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vm with id: " + vmId + " is online");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteVm(@PathVariable(name = "id") Long vmId) {
        try{
            vmService.deleteVm(vmId);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
        } catch (VmNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vm with id: " + vmId + " not found");
        } catch (NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        } catch (VmOnlineException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vm with id: " + vmId + " is online");
        }
    }

    @PostMapping("/{id}/on")
    public void turnOnVm(@PathVariable(name = "id") Long vmId) {
        try{
            vmService.turnOnVm(vmId);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
        } catch (VmNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vm with id: " + vmId + " not found");
        } catch (NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        } catch (TeamVmsResourcesLimitsExceededException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too many active VMs");
        }
    }

    @PostMapping("/{id}/off")
    public void turnOffVm(@PathVariable(name = "id") Long vmId) {
        try{
            vmService.turnOffVm(vmId);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
        } catch (VmNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vm with id: " + vmId + " not found");
        } catch (NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        }
    }

    @GetMapping("/models/{id}")
    public VmModelDTO getVmModel(@PathVariable(name = "id") Long vmModelId) {
        try{
            return vmService.getVmModel(vmModelId);
        } catch (VmModelNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vm Model with id: " + vmModelId + " not found");
        } catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        }
    }

    @PostMapping("/models")
    @ResponseStatus(HttpStatus.CREATED)
    public VmModelDTO addVmModel(@RequestBody Map<String, String> input) {
        if(!input.containsKey("courseCode") || !input.containsKey("name") || !input.containsKey("configuration"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");

        try{
            return vmService.addVmModel(input.get("courseCode"), input.get("name"), input.get("configuration"));
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, input.get("courseCode") + ": Course Not Found");
        } catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        } catch (VmModelAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course " + input.get("courseCode") + "already has a Vm Model");
        }
    }

    @PutMapping("/models/{id}")
    public VmModelDTO updateVmModel(@PathVariable(name = "id") Long vmModelId, @RequestBody VmModelDTO vmModelDTO) {
        try{
            return vmService.updateVmModel(vmModelId, vmModelDTO);
        } catch (VmModelNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vm Model with id: " + vmModelId + " not found");
        } catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        }
    }

}

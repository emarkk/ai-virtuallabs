package it.polito.ai.virtuallabs.backend.controllers;

import it.polito.ai.virtuallabs.backend.dtos.VmConfigurationLimitsDTO;
import it.polito.ai.virtuallabs.backend.dtos.VmDTO;
import it.polito.ai.virtuallabs.backend.dtos.VmModelDTO;
import it.polito.ai.virtuallabs.backend.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
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
        if(!input.containsKey("teamId") || !input.containsKey("vCpus") || !input.containsKey("diskSpace") || !input.containsKey("ram")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
        }
        try{
            return vmService.addVm(Long.parseLong(input.get("teamId")), Integer.parseInt(input.get("vCpus")), Integer.parseInt(input.get("diskSpace")), Integer.parseInt(input.get("ram")));
        } catch (TeamNotActiveException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team is not active");
        } catch (StudentNotInTeamException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student not in team");
        } catch (TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team Not Found");
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
        } catch (IllegalVmConfigurationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vm configuration not allowed");
        } catch (VmInstancesLimitNumberException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too many VMs for this team");
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
            if(!input.containsKey("diskSpace") || !input.containsKey("ram") || !input.containsKey("vCpus"))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
            return vmService.updateVm(vmId, input.get("vCpus"), input.get("diskSpace"), input.get("ram"));
        } catch (VmNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vm with id: " + vmId + " not found");
        } catch (StudentNotInTeamException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student not in team");
        } catch (TeamNotActiveException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team is not active");
        } catch (IllegalVmConfigurationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vm configuration not allowed");
        } catch (VmOnlineException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vm with id: " + vmId + " is online");
        }
    }

    @PatchMapping("/{id}/owners")
    public List<Boolean> addVmOwners(@PathVariable(name = "id") Long vmId, @RequestBody List<Long> studentIds) {
        try{
           return vmService.addVmOwners(vmId, studentIds);
        } catch (VmNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vm with id: " + vmId + " not found");
        } catch (IllegalVmOwnerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requesting student is not an owner");
        } catch (StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student Not Found");
        } catch (StudentNotInTeamException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student does not belong to vm team");
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
        } catch (IllegalVmOwnerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requesting student is not an owner");
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
        } catch (IllegalVmOwnerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requesting student is not an owner");
        } catch (VmActiveInstancesLimitNumberException e) {
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
        } catch (IllegalVmOwnerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requesting student is not an owner");
        }
    }

    @GetMapping("/{id}/connect")
    public ResponseEntity<Resource> connectVm(@PathVariable(name = "id") Long vmId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            byte[] file = vmService.connectVm(vmId);
            Resource resource = new ByteArrayResource(file);
            headers.setCacheControl(CacheControl.noCache().getHeaderValue());
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (VmNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vm with id: " + vmId + " not found");
        } catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        } catch (StudentNotInTeamException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student not in team");
        } catch (VmConnectionException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during vm connection");
        } catch (VmOfflineException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vm is offline");
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

    @PostMapping("/configurations")
    @ResponseStatus(HttpStatus.CREATED)
    public VmConfigurationLimitsDTO addVmConfigurationLimit(@RequestBody Map<String, String> input) {
        if(!input.containsKey("teamId") || !input.containsKey("maxVCpus") || !input.containsKey("maxDiskSpace") || !input.containsKey("maxRam") || !input.containsKey("maxInstances") || !input.containsKey("maxActiveInstances"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
        try{
            return vmService.addVmConfigurationLimit(Long.parseLong(input.get("teamId")), Integer.parseInt(input.get("maxVCpus")), Integer.parseInt(input.get("maxDiskSpace")), Integer.parseInt(input.get("maxRam")), Integer.parseInt(input.get("maxInstances")), Integer.parseInt(input.get("maxActiveInstances")));
        }  catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
        } catch (TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team Not Found");
        } catch (TeamNotActiveException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team is not active");
        } catch (VmConfigurationLimitsAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vm configuration already exists for team with id :" + input.get("teamId"));
        } catch (IllegalVmConfigurationLimitsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This configuration is not allowed");
        }
    }

    @GetMapping("/configurations/{id}")
    public VmConfigurationLimitsDTO getVmConfigurationLimits(@PathVariable(name = "id") Long vmConfigurationLimitsId) {
        try{
            return vmService.getVmConfigurationLimits(vmConfigurationLimitsId);
        } catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        } catch (VmConfigurationLimitsNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Configuration limits Not Found");
        } catch (StudentNotInTeamException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student not in team");
        }
    }

    @PutMapping("/configurations/{id}")
    public VmConfigurationLimitsDTO updateVmConfigurationLimits(@PathVariable(name = "id") Long vmConfigurationLimitsId, @RequestBody Map<String, Integer> input) {
        if( !input.containsKey("maxVCpus") || !input.containsKey("maxDiskSpace") || !input.containsKey("maxRam") || !input.containsKey("maxInstances") || !input.containsKey("maxActiveInstances"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
        try{
            return vmService.updateVmConfigurationLimits(vmConfigurationLimitsId, input.get("maxVCpus"), input.get("maxDiskSpace"), input.get("maxRam"), input.get("maxInstances"), input.get("maxActiveInstances"));
        } catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        } catch (VmConfigurationLimitsNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Configuration limits Not Found");
        } catch (IllegalVmConfigurationLimitsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This configuration is not allowed");
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
        }
    }
}

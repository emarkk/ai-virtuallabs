package it.polito.ai.virtuallabs.backend.controllers;

import it.polito.ai.virtuallabs.backend.dtos.VmModelDTO;
import it.polito.ai.virtuallabs.backend.services.CourseNotFoundException;
import it.polito.ai.virtuallabs.backend.services.NotAllowedException;
import it.polito.ai.virtuallabs.backend.services.VmModelAlreadyExistsException;
import it.polito.ai.virtuallabs.backend.services.VmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/vms")
public class VmController {

    @Autowired
    VmService vmService;

    @PostMapping({ "", "/" })
    @ResponseStatus(HttpStatus.CREATED)
    public VmModelDTO addVmModel(@RequestBody Map<String, String> input) {
        if(!input.containsKey("courseCode") || !input.containsKey("vmModelName") || !input.containsKey("vmModelConfiguration")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
        }
        try{
            return vmService.addVmModel(input.get("courseCode"), input.get("vmModelName"), input.get("vmModelConfiguration"));
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, input.get("courseCode") + ": Course Not Found");
        } catch(NotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient authorization");
        } catch (VmModelAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course " + input.get("courseCode") + "already has a Vm Model");
        }
    }

}

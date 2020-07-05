package it.polito.ai.virtuallabs.backend.controllers;

import it.polito.ai.virtuallabs.backend.dtos.CredentialsDTO;
import it.polito.ai.virtuallabs.backend.services.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/signup")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping({ "", "/" })
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody CredentialsDTO credentialsDTO) {
        if(!registrationService.addUser(credentialsDTO))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
    }

    @GetMapping("/confirm/{token}")
    public void confirm(@PathVariable("token") String token) {
        if(!registrationService.confirmUser(token))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token is invalid or expired");
    }

}

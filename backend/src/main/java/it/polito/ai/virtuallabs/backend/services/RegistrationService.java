package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.CredentialsDTO;

public interface RegistrationService {
    boolean addUser(CredentialsDTO credentialsDTO);
    boolean confirmUser(String token);
}

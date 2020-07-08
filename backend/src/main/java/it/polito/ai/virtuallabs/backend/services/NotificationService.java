package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.CredentialsDTO;


public interface NotificationService {

    void sendMessage(String address, String subject, String body);

    void notifyNewUser(CredentialsDTO credentialsDTO, String token);

    }

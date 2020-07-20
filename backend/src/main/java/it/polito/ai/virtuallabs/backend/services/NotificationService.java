package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.CredentialsDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamProposalDTO;

import java.util.List;


public interface NotificationService {

    void sendMessage(String address, String subject, String body);

    void notifyNewUser(CredentialsDTO credentialsDTO, String token);

    void notifyNewGroupProposal(TeamProposalDTO teamProposalDTO);

    }

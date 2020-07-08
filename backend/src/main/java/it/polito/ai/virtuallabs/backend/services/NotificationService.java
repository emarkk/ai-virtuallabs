package it.polito.ai.virtuallabs.backend.services;

public interface NotificationService {

    void sendMessage(String address, String subject, String body);

}

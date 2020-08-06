package it.polito.ai.virtuallabs.backend.services;

import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;

public interface HomeworkService {
    void addHomework(String courseCode, String title, Long dueDate, MultipartFile file);
}

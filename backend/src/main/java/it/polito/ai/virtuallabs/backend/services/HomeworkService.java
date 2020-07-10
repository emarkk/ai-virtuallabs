package it.polito.ai.virtuallabs.backend.services;

import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;

public interface HomeworkService {
    public void storeHomework(MultipartFile file, String courseCode, long dueDate);
}

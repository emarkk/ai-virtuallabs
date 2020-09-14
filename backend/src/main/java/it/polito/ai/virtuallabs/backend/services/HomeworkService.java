package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.HomeworkActionDTO;
import it.polito.ai.virtuallabs.backend.dtos.HomeworkDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HomeworkService {
    void addHomework(String courseCode, String title, Long dueDate, MultipartFile file);
    HomeworkDTO getHomework(Long homeworkId);
    Resource getHomeworkText(Long homeworkId);
    void deleteHomework(Long homeworkId);
    void addHomeworkDelivery(Long homeworkId, MultipartFile file);
    Resource getHomeworkDeliveryResource(Long homeworkDeliveryId);
    HomeworkActionDTO getHomeworkAction(Long homeworkActionId);
    List<HomeworkActionDTO> getHomeworkActions(Long homeworkId);
    List<HomeworkActionDTO> getStudentHomeworkActions(Long homeworkId, Long studentId);

}

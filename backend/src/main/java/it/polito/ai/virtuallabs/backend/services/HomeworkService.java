package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.HomeworkActionDTO;
import it.polito.ai.virtuallabs.backend.dtos.HomeworkDTO;
import it.polito.ai.virtuallabs.backend.dtos.PageDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HomeworkService {
    void addHomework(String courseCode, String title, Long dueDate, MultipartFile file);
    HomeworkDTO getHomework(Long homeworkId);
    Resource getHomeworkText(Long homeworkId);
    void deleteHomework(Long homeworkId);
    void addHomeworkDelivery(Long homeworkId, MultipartFile file);
    void addHomeworkReview(Long homeworkId, Long actionId, MultipartFile file, Integer mark);
    Resource getHomeworkActionResource(Long homeworkActionId);
    PageDTO<HomeworkActionDTO> getAllHomeworkActions(Long homeworkId, Integer page, Integer pageSize, String filterBy);
    List<HomeworkActionDTO> getStudentHomeworkActions(Long homeworkId, Long studentId);
}

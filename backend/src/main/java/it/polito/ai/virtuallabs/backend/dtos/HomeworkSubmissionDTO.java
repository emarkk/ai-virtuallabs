package it.polito.ai.virtuallabs.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class HomeworkSubmissionDTO {

    private Long id;

    private Timestamp delivered;

    private String solutionFilePath;

}

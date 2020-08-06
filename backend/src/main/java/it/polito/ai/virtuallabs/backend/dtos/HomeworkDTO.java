package it.polito.ai.virtuallabs.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeworkDTO {

    private Long id;

    private String title;

    private Timestamp publicationDate;

    private Timestamp dueDate;

}

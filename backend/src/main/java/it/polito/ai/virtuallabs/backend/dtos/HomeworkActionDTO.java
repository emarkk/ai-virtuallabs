package it.polito.ai.virtuallabs.backend.dtos;

import it.polito.ai.virtuallabs.backend.entities.HomeworkAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkActionDTO {

    private Long id;

    private Timestamp date;

    private HomeworkAction.ActionType actionType;

    private StudentDTO student;
}

package it.polito.ai.virtuallabs.backend.dtos;

import it.polito.ai.virtuallabs.backend.entities.TeamStudent;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamMemberStatusDTO {

    private StudentDTO student;

    private TeamStudent.InvitationStatus status;

}

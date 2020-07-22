package it.polito.ai.virtuallabs.backend.dtos;

import it.polito.ai.virtuallabs.backend.entities.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO {

    private Long id;

    private String name;

    private Team.FormationStatus status;

    private Timestamp invitationsExpiration;

    private Timestamp lastAction;

}

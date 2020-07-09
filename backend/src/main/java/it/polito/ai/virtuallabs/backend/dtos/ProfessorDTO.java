package it.polito.ai.virtuallabs.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorDTO {

    private Long id;

    private String email;

    private String firstName;

    private String lastName;

    private boolean hasPicture;

}

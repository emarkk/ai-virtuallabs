package it.polito.ai.virtuallabs.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfessorDTO {

    private Long id;

    private String email;

    private String firstName;

    private String lastName;

    private String picturePath;

}

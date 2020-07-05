package it.polito.ai.virtuallabs.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CredentialsDTO {

    private Long id;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

}
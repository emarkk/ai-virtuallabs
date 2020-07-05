package it.polito.ai.virtuallabs.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamDTO {

    private Long id;

    private String name;

    private Integer status;

}

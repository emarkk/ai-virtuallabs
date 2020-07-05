package it.polito.ai.virtuallabs.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {

    private String code;

    private String name;

    private String acronym;

    private Integer minTeamMembers;

    private Integer maxTeamMembers;

    private Boolean enabled;

}

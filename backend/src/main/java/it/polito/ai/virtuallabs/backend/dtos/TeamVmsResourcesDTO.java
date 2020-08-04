package it.polito.ai.virtuallabs.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamVmsResourcesDTO {

    private Integer vCpus;

    private Integer diskSpace;

    private Integer ram;

    private Integer instances;

    private Integer activeInstances;

}

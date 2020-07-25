package it.polito.ai.virtuallabs.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VmDTO {
    private Long id;
    private Integer vCpus;
    private Long diskSpace;
    private Long ram;
    private Boolean online;
}

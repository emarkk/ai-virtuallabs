package it.polito.ai.virtuallabs.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VmConfigurationLimitsDTO {

    private Long id;

    private Integer maxVCpus;

    private Integer maxDiskSpace;

    private Integer maxRam;

    private Integer maxInstances;

    private Integer maxActiveInstances;

}

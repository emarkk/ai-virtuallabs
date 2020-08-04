package it.polito.ai.virtuallabs.backend.websockets.signals;

import it.polito.ai.virtuallabs.backend.dtos.VmConfigurationLimitsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VmsResourcesSignal {
    public enum UpdateType {
        USED,
        TOTAL
    }

    private VmConfigurationLimitsDTO configurationLimitsDTO;

    private VmsResourcesSignal.UpdateType updateType;

}

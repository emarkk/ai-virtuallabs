package it.polito.ai.virtuallabs.backend.websockets.signals;

import it.polito.ai.virtuallabs.backend.dtos.TeamVmsResourcesDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamVmsResourcesSignal {

    public enum UpdateType {
        USED,
        TOTAL
    }

    private TeamVmsResourcesDTO teamVmsResources;

    private UpdateType updateType;

}

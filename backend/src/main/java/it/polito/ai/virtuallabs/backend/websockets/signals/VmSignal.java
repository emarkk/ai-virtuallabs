package it.polito.ai.virtuallabs.backend.websockets.signals;

import it.polito.ai.virtuallabs.backend.dtos.VmDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VmSignal {

    public enum UpdateType {
        CREATED,
        UPDATED,
        DELETED
    }

    private VmDTO vm;

    private UpdateType updateType;

}

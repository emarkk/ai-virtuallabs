package it.polito.ai.virtuallabs.backend.websockets.signals;

import it.polito.ai.virtuallabs.backend.dtos.VmDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VmState {

    public enum Status {
        UPDATED,
        DELETED
    }

    private VmDTO vm;

    private Status status;

}

package it.polito.ai.virtuallabs.backend.websockets;

import it.polito.ai.virtuallabs.backend.entities.Team;
import it.polito.ai.virtuallabs.backend.entities.Vm;

public interface SignalService {
    void vmCreated(Vm vm);
    void vmUpdated(Vm vm);
    void vmDeleted(Vm vm);
    void teamVmsResourcesLimitsChanged(Team team);
}

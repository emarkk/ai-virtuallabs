package it.polito.ai.virtuallabs.backend.websockets;

import it.polito.ai.virtuallabs.backend.dtos.VmDTO;
import it.polito.ai.virtuallabs.backend.entities.Team;
import it.polito.ai.virtuallabs.backend.entities.Vm;
import it.polito.ai.virtuallabs.backend.websockets.signals.VmSignal;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SignalService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void vmCreated(Vm vm) {
        this.signalVmStateChanged(vm, VmSignal.UpdateType.CREATED);
    }

    public void vmUpdated(Vm vm) {
        this.signalVmStateChanged(vm, VmSignal.UpdateType.UPDATED);
    }

    public void vmDeleted(Vm vm) {
        this.signalVmStateChanged(vm, VmSignal.UpdateType.DELETED);
    }

    public void vmsConfigurationLimitsUpdated(Team team) {
    }

    private void signalVmStateChanged(Vm vm, VmSignal.UpdateType updateType) {
        VmSignal vmSignal = new VmSignal(modelMapper.map(vm, VmDTO.class), updateType);
        messagingTemplate.convertAndSend("/vm/" + vm.getId(), vmSignal);
        messagingTemplate.convertAndSend("/team/" + vm.getTeam().getId() + "/vms", vmSignal);
        messagingTemplate.convertAndSend("/course/" + vm.getTeam().getCourse().getCode() + "/vms", vmSignal);
    }

    private void signalVmsResourcesUsageChanged() {

    }

}

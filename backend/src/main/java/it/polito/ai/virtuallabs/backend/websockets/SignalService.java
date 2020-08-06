package it.polito.ai.virtuallabs.backend.websockets;

import it.polito.ai.virtuallabs.backend.dtos.TeamVmsResourcesDTO;
import it.polito.ai.virtuallabs.backend.dtos.VmDTO;
import it.polito.ai.virtuallabs.backend.entities.Team;
import it.polito.ai.virtuallabs.backend.entities.Vm;
import it.polito.ai.virtuallabs.backend.websockets.signals.TeamVmsResourcesSignal;
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

    public void teamVmsResourcesLimitsChanged(Team team) {
        TeamVmsResourcesSignal vmsResourcesSignal = new TeamVmsResourcesSignal(
                modelMapper.map(team.getVmsResourcesLimits(), TeamVmsResourcesDTO.class),
                TeamVmsResourcesSignal.UpdateType.TOTAL);
        messagingTemplate.convertAndSend("/team/" + team.getId() + "/vms-resources", vmsResourcesSignal);
    }

    private void signalVmStateChanged(Vm vm, VmSignal.UpdateType updateType) {
        VmSignal vmSignal = new VmSignal(modelMapper.map(vm, VmDTO.class), vm.getTeam().getId(), updateType);
        messagingTemplate.convertAndSend("/vm/" + vm.getId(), vmSignal);
        messagingTemplate.convertAndSend("/team/" + vm.getTeam().getId() + "/vms", vmSignal);
        messagingTemplate.convertAndSend("/course/" + vm.getTeam().getCourse().getCode() + "/vms", vmSignal);

        TeamVmsResourcesSignal vmsResourcesSignal = new TeamVmsResourcesSignal(
                modelMapper.map(vm.getTeam().getVmsResourcesUsed(), TeamVmsResourcesDTO.class),
                TeamVmsResourcesSignal.UpdateType.USED);
        messagingTemplate.convertAndSend("/team/" + vm.getTeam().getId() + "/vms-resources", vmsResourcesSignal);
    }

}

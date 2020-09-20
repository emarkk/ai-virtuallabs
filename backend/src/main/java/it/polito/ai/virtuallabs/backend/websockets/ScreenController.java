package it.polito.ai.virtuallabs.backend.websockets;

import it.polito.ai.virtuallabs.backend.dtos.ProfessorDTO;
import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import it.polito.ai.virtuallabs.backend.entities.AuthenticatedEntity;
import it.polito.ai.virtuallabs.backend.entities.Professor;
import it.polito.ai.virtuallabs.backend.entities.Student;
import it.polito.ai.virtuallabs.backend.entities.Vm;
import it.polito.ai.virtuallabs.backend.utils.GetterProxy;
import it.polito.ai.virtuallabs.backend.websockets.signals.VmScreenSignal;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.awt.*;
import java.util.*;

@Controller
public class ScreenController implements ApplicationListener<SessionUnsubscribeEvent> {

    @Autowired
    private GetterProxy getterProxy;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final Map<String, String> sessions = new HashMap<>();
    private final Map<Long, Set<ProfessorDTO>> connectedProfessors = new HashMap<>();
    private final Map<Long, Set<StudentDTO>> connectedStudents = new HashMap<>();

    @SubscribeMapping("/vm/{vmId}/screen")
    @SendTo("/vm/{vmId}/screen")
    public VmScreenSignal screenSubscribe(@DestinationVariable Long vmId, Message<?> message) {
        Vm vm = getterProxy.vm(vmId);
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if(!this.connectedProfessors.containsKey(vm.getId()))
            this.connectedProfessors.put(vm.getId(), new HashSet<>());
        if(!this.connectedStudents.containsKey(vm.getId()))
            this.connectedStudents.put(vm.getId(), new HashSet<>());

        AuthenticatedEntity user = getterProxy.authenticatedEntity(accessor);
        if(user instanceof Professor) {
            Professor professor = getterProxy.professor(((Professor) user).getId());
            this.sessions.put(accessor.getSessionId() + "/" + accessor.getSubscriptionId(), vmId + "/" + ((Professor) user).getId());
            this.connectedProfessors.get(vm.getId()).add(modelMapper.map(professor, ProfessorDTO.class));
        } else if(user instanceof Student) {
            Student student = getterProxy.student(((Student) user).getId());
            this.sessions.put(accessor.getSessionId() + "/" + accessor.getSubscriptionId(), vmId + "/" + ((Student) user).getId());
            this.connectedStudents.get(vm.getId()).add(modelMapper.map(student, StudentDTO.class));
        }

        return new VmScreenSignal(
                vm.getOnline(),
                vm.getTeam().getName(),
                new ArrayList<>(this.connectedProfessors.get(vm.getId())),
                new ArrayList<>(this.connectedStudents.get(vm.getId()))
        );
    }

    @Override
    public void onApplicationEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String data = this.sessions.get(accessor.getSessionId() + "/" + accessor.getSubscriptionId());
        Long vmId = Long.parseLong(data.split("/")[0]);
        Long userId = Long.parseLong(data.split("/")[1]);

        this.sessions.remove(accessor.getSessionId() + "/" + accessor.getSubscriptionId());
        this.connectedProfessors.get(vmId).removeIf(p -> p.getId().equals(userId));
        this.connectedStudents.get(vmId).removeIf(p -> p.getId().equals(userId));

        messagingTemplate.convertAndSend("/vm/" + vmId + "/screen", new VmScreenSignal(
                null,
                null,
                new ArrayList<>(this.connectedProfessors.get(vmId)),
                new ArrayList<>(this.connectedStudents.get(vmId)))
        );
    }

}

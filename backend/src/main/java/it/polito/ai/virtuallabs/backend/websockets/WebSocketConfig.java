package it.polito.ai.virtuallabs.backend.websockets;

import it.polito.ai.virtuallabs.backend.entities.*;
import it.polito.ai.virtuallabs.backend.services.*;
import it.polito.ai.virtuallabs.backend.utils.GetterProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import static org.springframework.messaging.support.MessageBuilder.createMessage;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private GetterProxy getterProxy;

    @Autowired
    private CourseService courseService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private VmService vmService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("");
        config.setApplicationDestinationPrefixes("");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/signals").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

                if(StompCommand.CONNECT.equals(accessor.getCommand()) && !authorizeConnection(accessor))
                    throw new InvalidUserException();

                if(StompCommand.SUBSCRIBE.equals(accessor.getCommand()) && !authorizeSubscription(accessor))
                    throw new NotAllowedException();

                if(StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                    accessor.setSubscriptionId("$ALL$");
                    eventPublisher.publishEvent(new SessionUnsubscribeEvent(this, (Message)MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders())));
                }

                return message;
            }
        });
    }

    private boolean authorizeConnection(StompHeaderAccessor accessor) {
        return getterProxy.authenticatedEntity(accessor) != null;
    }

    private boolean authorizeSubscription(StompHeaderAccessor accessor) {
        AuthenticatedEntity user = getterProxy.authenticatedEntity(accessor);
        String destination = accessor.getDestination();

        if(destination == null)
            return false;

        // path /vm/{id}
        if(destination.matches("/vm/\\d+")) {
            Long vmId = Long.parseLong(destination.replaceFirst("/vm/(\\d+)", "$1"));
            return (user instanceof Student && vmService.studentHasSignalPermission(vmId, ((Student) user).getId()))
                    || (user instanceof Professor && vmService.professorHasSignalPermission(vmId, ((Professor) user).getId()));
        }
        // path /vm/{id}/screen
        if(destination.matches("/vm/\\d+/screen")) {
            Long vmId = Long.parseLong(destination.replaceFirst("/vm/(\\d+)/screen", "$1"));
            return (user instanceof Student && vmService.studentHasSignalPermission(vmId, ((Student) user).getId()))
                    || (user instanceof Professor && vmService.professorHasSignalPermission(vmId, ((Professor) user).getId()));
        }
        // path /team/{id}/vms
        if(destination.matches("/team/\\d+/vms")) {
            Long teamId = Long.parseLong(destination.replaceFirst("/team/(\\d+)/vms", "$1"));
            return user instanceof Student && teamService.studentHasSignalPermission(teamId, ((Student) user).getId());
        }
        // path /team/{id}/vms-resources
        if(destination.matches("/team/\\d+/vms-resources")) {
            Long teamId = Long.parseLong(destination.replaceFirst("/team/(\\d+)/vms-resources", "$1"));
            return (user instanceof Student && teamService.studentHasSignalPermission(teamId, ((Student) user).getId()))
                    || (user instanceof Professor && teamService.professorHasSignalPermission(teamId, ((Professor) user).getId()));
        }
        // path /course/{id}/vms
        if(destination.matches("/course/\\w+/vms")) {
            String courseCode = destination.replaceFirst("/course/(\\w+)/vms", "$1");
            return user instanceof Professor && courseService.professorHasSignalPermission(courseCode, ((Professor) user).getId());
        }

        return false;
    }
}

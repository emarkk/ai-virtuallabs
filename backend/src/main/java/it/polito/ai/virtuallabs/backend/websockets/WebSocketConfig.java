package it.polito.ai.virtuallabs.backend.websockets;

import it.polito.ai.virtuallabs.backend.entities.*;
import it.polito.ai.virtuallabs.backend.security.AuthenticatedEntityMapper;
import it.polito.ai.virtuallabs.backend.security.jwt.JwtTokenProvider;
import it.polito.ai.virtuallabs.backend.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticatedEntityMapper authenticatedEntityMapper;

    @Autowired
    private CourseService courseService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private VmService vmService;

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

                return message;
            }
        });
    }

    private AuthenticatedEntity getUser(StompHeaderAccessor accessor) {
        String token = accessor.getFirstNativeHeader("token");
        if(token != null && jwtTokenProvider.validateToken(token))
            return authenticatedEntityMapper.getByAuthentication(jwtTokenProvider.getAuthentication(token));
        return null;
    }

    private boolean authorizeConnection(StompHeaderAccessor accessor) {
        return this.getUser(accessor) != null;
    }

    private boolean authorizeSubscription(StompHeaderAccessor accessor) {
        AuthenticatedEntity user = this.getUser(accessor);
        String destination = accessor.getDestination();

        if(destination == null)
            return false;

        if(destination.matches("/vm/\\d+")) {
            Long vmId = Long.parseLong(destination.replaceFirst("/vm/(\\d+)", "$1"));
            return (user instanceof Student && vmService.studentHasSignalPermission(vmId, ((Student) user).getId()))
                    || (user instanceof Professor && vmService.professorHasSignalPermission(vmId, ((Professor) user).getId()));
        }
        if(destination.matches("/team/\\d+/vms")) {
            Long teamId = Long.parseLong(destination.replaceFirst("/team/(\\d+)/vms", "$1"));
            return user instanceof Student && teamService.studentHasSignalPermission(teamId, ((Student) user).getId());
        }
        if(destination.matches("/team/\\d+/vm-limits")) {
            Long teamId = Long.parseLong(destination.replaceFirst("/team/(\\d+)/vm-limits", "$1"));
            return (user instanceof Student && teamService.studentHasSignalPermission(teamId, ((Student) user).getId()))
                    || (user instanceof Professor && teamService.professorHasSignalPermission(teamId, ((Professor) user).getId()));
        }
        if(destination.matches("/course/\\w+/vms")) {
            String courseCode = destination.replaceFirst("/course/(\\w+)/vms", "$1");
            return user instanceof Professor && courseService.professorHasSignalPermission(courseCode, ((Professor) user).getId());
        }

        return false;
    }

}

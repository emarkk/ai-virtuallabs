package it.polito.ai.virtuallabs.backend.security;

import it.polito.ai.virtuallabs.backend.entities.AuthenticatedEntity;
import org.springframework.security.core.Authentication;

public interface AuthenticatedEntityMapper {
    AuthenticatedEntity get();
    AuthenticatedEntity getByAuthentication(Authentication authentication);
}

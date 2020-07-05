package it.polito.ai.virtuallabs.backend.security;

import it.polito.ai.virtuallabs.backend.entities.AuthenticatedEntity;

public interface AuthenticatedEntityMapper {
    AuthenticatedEntity get();
}

package it.polito.ai.virtuallabs.backend.security;

import it.polito.ai.virtuallabs.backend.entities.Actor;

public interface AuthenticatedEntity {
    Actor get();
}

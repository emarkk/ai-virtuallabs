package it.polito.ai.virtuallabs.backend.security;

import it.polito.ai.virtuallabs.backend.entities.AuthenticatedEntity;
import it.polito.ai.virtuallabs.backend.repositories.ProfessorRepository;
import it.polito.ai.virtuallabs.backend.repositories.StudentRepository;
import it.polito.ai.virtuallabs.backend.services.InvalidUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedEntityMapperImpl implements AuthenticatedEntityMapper {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Override
    public AuthenticatedEntity get() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long id = Long.parseLong(username.substring(1));

        if(username.startsWith("s"))
            return studentRepository.getOne(id);
        else if(username.startsWith("d"))
            return professorRepository.getOne(id);
        else throw new InvalidUserException();
    }

}

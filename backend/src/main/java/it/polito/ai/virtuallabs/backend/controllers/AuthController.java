package it.polito.ai.virtuallabs.backend.controllers;

import it.polito.ai.virtuallabs.backend.dtos.AuthenticationRequestDTO;
import it.polito.ai.virtuallabs.backend.entities.User;
import it.polito.ai.virtuallabs.backend.repositories.UserRepository;
import it.polito.ai.virtuallabs.backend.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserRepository userRepository;

    @PostMapping({ "", "/" })
    public ResponseEntity login(@RequestBody AuthenticationRequestDTO data) {
        try {
            // users can login with id or email
            String credential = data.getUsername().toLowerCase();

            Optional<User> userOptional = this.userRepository.findByUsername(credential);
            if(userOptional.isEmpty()) {
                userOptional = this.userRepository.findByEmail(credential);
                if(userOptional.isEmpty())
                    throw new UsernameNotFoundException("Username/email not found");
            }

            User user = userOptional.get();
            String username = user.getUsername();

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
            String token = jwtTokenProvider.createToken(username, user.getRoles());
            Map<Object, Object> model = new HashMap<>();
            model.put("username", username);
            model.put("token", token);
            return ok(model);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }
}
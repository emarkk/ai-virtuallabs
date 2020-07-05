package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.CredentialsDTO;
import it.polito.ai.virtuallabs.backend.entities.Professor;
import it.polito.ai.virtuallabs.backend.entities.Student;
import it.polito.ai.virtuallabs.backend.entities.User;
import it.polito.ai.virtuallabs.backend.repositories.ProfessorRepository;
import it.polito.ai.virtuallabs.backend.repositories.StudentRepository;
import it.polito.ai.virtuallabs.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean addUser(CredentialsDTO credentialsDTO) {
        String role = "", username = "";

        if (credentialsDTO.getEmail().endsWith("@studenti.polito.it")) {
            role = "ROLE_STUDENT";
            username = "s" + credentialsDTO.getId();
        } else if (credentialsDTO.getEmail().endsWith("@polito.it")) {
            role = "ROLE_PROFESSOR";
            username = "d" + credentialsDTO.getId();
        } else throw new InvalidUserException();

        if(userRepository.existsByEmail(credentialsDTO.getEmail()) || userRepository.existsByUsername(username))
            return false;

        User user = User.builder()
                .id(null)
                .username(username)
                .email(credentialsDTO.getEmail())
                .password(passwordEncoder.encode(credentialsDTO.getPassword()))
                .enabled(false)
                .registration(new Timestamp(System.currentTimeMillis()))
                .emailVerificationToken(UUID.randomUUID().toString())
                .roles(List.of(role))
                .build();
        userRepository.save(user);

        if(role.equals("ROLE_STUDENT")) {
            Student student = Student.builder()
                    .id(credentialsDTO.getId())
                    .email(credentialsDTO.getEmail())
                    .firstName(credentialsDTO.getFirstName())
                    .lastName(credentialsDTO.getLastName())
                    .build();
            studentRepository.save(student);
        } else {
            Professor professor = Professor.builder()
                    .id(credentialsDTO.getId())
                    .email(credentialsDTO.getEmail())
                    .firstName(credentialsDTO.getFirstName())
                    .lastName(credentialsDTO.getLastName())
                    .build();
            professorRepository.save(professor);
        }

        return true;
    }

    public boolean confirmUser(String token) {
        Optional<User> userOptional = userRepository.findByEmailVerificationToken(token);

        if(userOptional.isEmpty())
            return false;

        User user = userOptional.get();
        user.setEnabled(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);
        return true;
    }

}

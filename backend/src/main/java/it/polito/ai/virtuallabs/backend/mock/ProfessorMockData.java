package it.polito.ai.virtuallabs.backend.mock;

import it.polito.ai.virtuallabs.backend.entities.Professor;
import it.polito.ai.virtuallabs.backend.entities.User;
import it.polito.ai.virtuallabs.backend.repositories.ProfessorRepository;
import it.polito.ai.virtuallabs.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ProfessorMockData {

    UserRepository userRepository;
    ProfessorRepository professorRepository;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ProfessorMockData(UserRepository userRepository, ProfessorRepository professorRepository) {
        this.userRepository = userRepository;
        this.professorRepository = professorRepository;
    }

    @Transactional
    public void add(Long id, String email, String password, String firstName, String lastName) {
        String username = "d" + id;
        String resumed = username + " " + firstName.toLowerCase() + " " + lastName.toLowerCase();
        Professor professor = new Professor(id, email, firstName, lastName, false, resumed, new ArrayList<>());
        User user = new User(id, username, email, passwordEncoder.encode(password), true, new Timestamp(System.currentTimeMillis()), null, List.of("ROLE_PROFESSOR"));
        System.out.println(userRepository);
        userRepository.save(user);
        professorRepository.save(professor);
    }

    public void addProfessors() {
        add(14724L, "alessio.defranco@polito.it", "password", "Alessio", "De Franco");
        add(18329L, "giada.caputi@polito.it", "password", "Giada", "Caputi");
        add(25842L, "benedetta.tirone@polito.it", "password", "Benedetta", "Tirone");
        add(19428L, "fabio.capello@polito.it", "password", "Fabio", "Capello");
        add(13429L, "lorenzo.dedominicis@polito.it", "password", "Lorenzo", "De Dominicis");
        add(24393L, "leonardo.tirato@polito.it", "password", "Leonardo", "Tirato");
        add(22294L, "fabiana.canella@polito.it", "password", "Fabiana", "Canella");
    }

}

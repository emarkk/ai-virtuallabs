package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.CredentialsDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamDTO;
import it.polito.ai.virtuallabs.backend.dtos.TeamProposalDTO;
import it.polito.ai.virtuallabs.backend.entities.User;
import it.polito.ai.virtuallabs.backend.repositories.ProfessorRepository;
import it.polito.ai.virtuallabs.backend.repositories.StudentRepository;
import it.polito.ai.virtuallabs.backend.repositories.UserRepository;
import it.polito.ai.virtuallabs.backend.security.AuthenticatedEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

@Transactional
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    JavaMailSender javaMailSender;


    @Autowired
    UserRepository userRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    ProfessorRepository professorRepository;

    @Async
    @Override
    public void sendMessage(String address, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("virtuallabs.notfier@gmail.com");
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }

    @Override
    public void notifyNewUser(CredentialsDTO credentialsDTO, String token) {
        sendMessage(credentialsDTO.getEmail(),
                "This is your Virtuallabs Account",
                "Hi " + credentialsDTO.getFirstName() + ",\na new Virtuallabs account named has been created for you.\n"
                        + "Please, click on the link here below in order to validate your account: http://localhost:3000/api/signup/confirm/" + token + "\n"
                        + "If you didn't request this subscription you can ignore this email.\n"
                );
    }

    @Override
    public void notifyNewGroupProposal(TeamProposalDTO teamProposalDTO) {
        teamProposalDTO.getMembersIds().forEach(s -> {
            String email = "s" + s + "@studenti.polito.it";
            sendMessage(
                    email,
                    "Invitation to new group " + teamProposalDTO.getName(),
                    "Hi " + email + "," +
                            "\n you received a new invitation to join a group.\n" +
                            "Please visit your account for additional details."
            );
        });
    }

    @Scheduled(initialDelay = 1000, fixedRate = 1000 * 60 * 60 * 24)
    public void scheduledExpiredUserClean() {
        Timestamp oneDayAgoTimestamp = new Timestamp(System.currentTimeMillis() - 1000 * 60 * 60 * 24);

        List<User> expiredAccounts = userRepository.findAllByRegistrationBeforeAndEmailVerificationTokenNotNull(oneDayAgoTimestamp);

        expiredAccounts.forEach(t -> {
            userRepository.delete(t);
            String username = t.getUsername();
            char role = username.charAt(0);
            long id = Long.parseLong(username.substring(1));
            System.out.println(role);
            if(role == 's'){
                studentRepository.deleteById(id);
            } else {
                System.out.println(id);
                professorRepository.deleteById(id);
            }
        });

    }


}

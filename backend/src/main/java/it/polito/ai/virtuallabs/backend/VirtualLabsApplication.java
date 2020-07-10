package it.polito.ai.virtuallabs.backend;

import it.polito.ai.virtuallabs.backend.mock.MockData;
import it.polito.ai.virtuallabs.backend.repositories.CourseRepository;
import it.polito.ai.virtuallabs.backend.repositories.ProfessorRepository;
import it.polito.ai.virtuallabs.backend.repositories.StudentRepository;
import it.polito.ai.virtuallabs.backend.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class VirtualLabsApplication {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfessorRepository professorRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @PostConstruct
    public void populateDb() {
        MockData mock = new MockData(userRepository, professorRepository, studentRepository, courseRepository);
        mock.insertMockData();
    }

    public static void main(String[] args) {
        SpringApplication.run(VirtualLabsApplication.class, args);
    }

}

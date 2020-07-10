package it.polito.ai.virtuallabs.backend;

import it.polito.ai.virtuallabs.backend.dtos.CourseDTO;
import it.polito.ai.virtuallabs.backend.dtos.CredentialsDTO;
import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Professor;
import it.polito.ai.virtuallabs.backend.entities.Student;
import it.polito.ai.virtuallabs.backend.entities.User;
import it.polito.ai.virtuallabs.backend.repositories.CourseRepository;
import it.polito.ai.virtuallabs.backend.repositories.ProfessorRepository;
import it.polito.ai.virtuallabs.backend.repositories.StudentRepository;
import it.polito.ai.virtuallabs.backend.repositories.UserRepository;
import it.polito.ai.virtuallabs.backend.services.RegistrationService;
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

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class VirtualLabsApplication {

    @Autowired
    StudentRepository studentRepository;
    @Autowired
    ProfessorRepository professorRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    RegistrationService registrationService;
    @Autowired
    UserRepository userRepository;


    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void dbEnrollStudentsInit() {
        Course c1 = courseRepository.findById("01NNEOV").get();
        Course c2 = courseRepository.findById("01NYHOV").get();
        Course c3 = courseRepository.findById("01PFPOV").get();

        Student s1 = studentRepository.findById((long)000001).get();
        c1.addStudent(s1);

        Student s2 = studentRepository.findById((long)000002).get();
        c1.addStudent(s2);
        c2.addStudent(s2);

        Student s3 = studentRepository.findById((long)000003).get();
        c1.addStudent(s3);

        Student s4 = studentRepository.findById((long)000004).get();
        c3.addStudent(s4);
        c2.addStudent(s4);

        Student s5 = studentRepository.findById((long)000005).get();
        c1.addStudent(s5);
        c2.addStudent(s5);
        c3.addStudent(s5);

        Student s6 = studentRepository.findById((long)000006).get();
        c1.addStudent(s6);
        c2.addStudent(s6);
        c3.addStudent(s6);

        Student s7 = studentRepository.findById((long)000007).get();
        c2.addStudent(s7);

        Student s8 = studentRepository.findById((long)8).get();
        c3.addStudent(s8);
        c1.addStudent(s8);

        Student s9 = studentRepository.findById((long)9).get();
        c2.addStudent(s9);

        Student s10 = studentRepository.findById((long)10).get();
        c1.addStudent(s10);
        c2.addStudent(s10);
        c3.addStudent(s10);

        courseRepository.save(c1); //Salva per tutti
        //courseRepository.save(c2); duplicazioni salvataggi
        //courseRepository.save(c3);

    }

    private void dbCourseInit() {
        Professor p1 = professorRepository.findById((long) 000001).get();
        Course c1 = Course.builder()
                .code("01NNEOV")
                .name("Modelli e Sistemi a Eventi Discreti")
                .acronym("MSED")
                .enabled(true)
                .minTeamMembers(3)
                .maxTeamMembers(6)
                .build();
        c1.addProfessor(p1);
        courseRepository.save(c1);

        Professor p2 = professorRepository.findById((long) 000002).get();
        Course c2 = Course.builder()
                .code("01NYHOV")
                .name("System and Device Programming")
                .acronym("SDP")
                .enabled(true)
                .minTeamMembers(2)
                .maxTeamMembers(4)
                .build();
        c2.addProfessor(p2);
        courseRepository.save(c2);


        Professor p3 = professorRepository.findById((long) 000003).get();
        Course c3 = Course.builder()
                .code("01PFPOV")
                .name("Mobile Application Development")
                .acronym("MAD")
                .enabled(true)
                .minTeamMembers(2)
                .maxTeamMembers(5)
                .build();
        c3.addProfessor(p3);
        courseRepository.save(c3);

        Professor p4 = professorRepository.findById((long) 000004).get();
        Course c4 = Course.builder()
                .code("01SQNOV")
                .name("Software Engineering II")
                .acronym("SEII")
                .enabled(true)
                .minTeamMembers(4)
                .maxTeamMembers(6)
                .build();
        c4.addProfessor(p4);
        c4 = courseRepository.save(c4);
    }

    void dbProfInit(){
        CredentialsDTO p1 = new CredentialsDTO((long) 000001, "d000001@polito.it", "password", "Mario", "Bianchi");
        registrationService.addUser(p1);
        User u1 = userRepository.findByEmail(p1.getEmail()).get();
        u1.setEnabled(true);
        u1.setEmailVerificationToken(null);
        userRepository.save(u1);

        CredentialsDTO p2 = new CredentialsDTO((long) 000002, "d000002@polito.it", "password", "Luigi", "Baldi");
        registrationService.addUser(p2);
        User u2 = userRepository.findByEmail(p2.getEmail()).get();
        u2.setEnabled(true);
        u2.setEmailVerificationToken(null);
        userRepository.save(u2);

        CredentialsDTO p3 = new CredentialsDTO((long) 000003, "d000003@polito.it", "password", "Marco", "Altini");
        registrationService.addUser(p3);
        User u3 = userRepository.findByEmail(p3.getEmail()).get();
        u3.setEnabled(true);
        u3.setEmailVerificationToken(null);
        userRepository.save(u3);

        CredentialsDTO p4 = new CredentialsDTO((long) 000004, "d000004@polito.it", "password", "Federico", "Borbone");
        registrationService.addUser(p4);
        User u4 = userRepository.findByEmail(p4.getEmail()).get();
        u4.setEnabled(true);
        u4.setEmailVerificationToken(null);
        userRepository.save(u4);

        CredentialsDTO p5 = new CredentialsDTO((long) 000005, "d000005@polito.it", "password", "Enzo", "Valli");
        registrationService.addUser(p5);
        User u5 = userRepository.findByEmail(p5.getEmail()).get();
        u5.setEnabled(true);
        u5.setEmailVerificationToken(null);
        userRepository.save(u5);
    }

    void dbStudentInit(){
        //Student init
        CredentialsDTO p1 = new CredentialsDTO((long) 000001, "s000001@studenti.polito.it", "password", "Valerio", "Valvo");
        registrationService.addUser(p1);
        User u1 = userRepository.findByEmail(p1.getEmail()).get();
        u1.setEnabled(true);
        u1.setEmailVerificationToken(null);
        userRepository.save(u1);


        CredentialsDTO p2 = new CredentialsDTO((long) 000002, "s000002@studenti.polito.it", "password", "Alberto", "Macchi");
        registrationService.addUser(p2);
        User u2 = userRepository.findByEmail(p2.getEmail()).get();
        u2.setEnabled(true);
        u2.setEmailVerificationToken(null);
        userRepository.save(u2);


        CredentialsDTO p3 = new CredentialsDTO((long) 000003, "s000003@studenti.polito.it", "password", "Giulio", "Cesare");
        registrationService.addUser(p3);
        User u3 = userRepository.findByEmail(p3.getEmail()).get();
        u3.setEnabled(true);
        u3.setEmailVerificationToken(null);
        userRepository.save(u3);


        CredentialsDTO p4 = new CredentialsDTO((long) 000004, "s000004@studenti.polito.it", "password", "Cesare", "Augusto");
        registrationService.addUser(p4);
        User u4 = userRepository.findByEmail(p4.getEmail()).get();
        u4.setEnabled(true);
        u4.setEmailVerificationToken(null);
        userRepository.save(u4);


        CredentialsDTO p5 = new CredentialsDTO((long) 000005, "s000005@studenti.polito.it", "password", "Carlo", "Cracco");
        registrationService.addUser(p5);
        User u5 = userRepository.findByEmail(p5.getEmail()).get();
        u5.setEnabled(true);
        u5.setEmailVerificationToken(null);
        userRepository.save(u5);


        CredentialsDTO p6 = new CredentialsDTO((long) 000006, "s000006@studenti.polito.it", "password", "Gino", "Bonelli");
        registrationService.addUser(p6);
        User u6 = userRepository.findByEmail(p6.getEmail()).get();
        u6.setEnabled(true);
        u6.setEmailVerificationToken(null);
        userRepository.save(u6);


        CredentialsDTO p7 = new CredentialsDTO((long) 000007, "s000007@studenti.polito.it", "password", "Michele", "Preziosi");
        registrationService.addUser(p7);
        User u7 = userRepository.findByEmail(p7.getEmail()).get();
        u7.setEnabled(true);
        u7.setEmailVerificationToken(null);
        userRepository.save(u7);


        CredentialsDTO p8 = new CredentialsDTO((long) 8, "s000008@studenti.polito.it", "password", "Giorgio", "Ambrosiano");
        registrationService.addUser(p8);
        User u8 = userRepository.findByEmail(p8.getEmail()).get();
        u8.setEnabled(true);
        u8.setEmailVerificationToken(null);
        userRepository.save(u8);


        CredentialsDTO p9 = new CredentialsDTO((long) 9, "s000009@studenti.polito.it", "password", "Valerio", "Valvo");
        registrationService.addUser(p9);
        User u9 = userRepository.findByEmail(p9.getEmail()).get();
        u9.setEnabled(true);
        u9.setEmailVerificationToken(null);
        userRepository.save(u9);


        CredentialsDTO p10 = new CredentialsDTO((long) 10, "s000010@studenti.polito.it", "password", "Andrea", "Rocchelli");
        registrationService.addUser(p10);
        User u10 = userRepository.findByEmail(p10.getEmail()).get();
        u10.setEnabled(true);
        u10.setEmailVerificationToken(null);
        userRepository.save(u10);


        CredentialsDTO p11 = new CredentialsDTO((long) 11, "s000011@studenti.polito.it", "password", "Antonio", "Giusto");
        registrationService.addUser(p11);
        User u11 = userRepository.findByEmail(p11.getEmail()).get();
        u11.setEnabled(true);
        u11.setEmailVerificationToken(null);
        userRepository.save(u11);


        CredentialsDTO p12 = new CredentialsDTO((long) 12, "s000012@studenti.polito.it", "password", "Riccardo", "Meleo");
        registrationService.addUser(p12);
        User u12 = userRepository.findByEmail(p12.getEmail()).get();
        u12.setEnabled(true);
        u12.setEmailVerificationToken(null);
        userRepository.save(u12);


        CredentialsDTO p13 = new CredentialsDTO((long) 13, "s000013@studenti.polito.it", "password", "Emanuele", "Micieli");
        registrationService.addUser(p13);
        User u13 = userRepository.findByEmail(p13.getEmail()).get();
        u13.setEnabled(true);
        u13.setEmailVerificationToken(null);
        userRepository.save(u13);


        CredentialsDTO p14 = new CredentialsDTO((long) 14, "s000014@studenti.polito.it", "password", "Costantino", "Valvo");
        registrationService.addUser(p14);
        User u14 = userRepository.findByEmail(p14.getEmail()).get();
        u14.setEnabled(true);
        u14.setEmailVerificationToken(null);
        userRepository.save(u14);


        CredentialsDTO p15 = new CredentialsDTO((long) 15, "s000015@studenti.polito.it", "password", "Renato", "Zero");
        registrationService.addUser(p15);
        User u15 = userRepository.findByEmail(p15.getEmail()).get();
        u15.setEnabled(true);
        u15.setEmailVerificationToken(null);
        userRepository.save(u15);
    }

    @Bean
    CommandLineRunner runner(StudentRepository studentRepository){
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                dbProfInit();
                dbStudentInit();
                dbCourseInit();
                dbEnrollStudentsInit();
            }
        };
    }


    public static void main(String[] args) {
        SpringApplication.run(VirtualLabsApplication.class, args);
    }

}

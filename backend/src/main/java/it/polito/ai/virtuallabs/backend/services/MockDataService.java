package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.entities.*;
import it.polito.ai.virtuallabs.backend.repositories.*;
import it.polito.ai.virtuallabs.backend.utils.ProfilePicturesUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MockDataService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfessorRepository professorRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    VmRepository vmRepository;

    @Autowired
    TeamStudentRepository teamStudentRepository;

    @Autowired
    VmModelRepository vmModelRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ProfilePicturesUtility profilePicturesUtility;

    public void addProfessor(Long id, String email, String password, String firstName, String lastName) {
        String username = "d" + id;
        Professor professor = new Professor(id, email, firstName, lastName, false, new ArrayList<>());
        User user = new User(id, username, email, passwordEncoder.encode(password), true, new Timestamp(System.currentTimeMillis()), null, List.of("ROLE_PROFESSOR"));
        userRepository.save(user);
        professorRepository.save(professor);
    }

    public void addProfessors() {
        addProfessor(14724L, "alessio.defranco@polito.it", "password", "Alessio", "De Franco");
        addProfessor(18329L, "giada.caputi@polito.it", "password", "Giada", "Caputi");
        addProfessor(25842L, "benedetta.tirone@polito.it", "password", "Benedetta", "Tirone");
        addProfessor(19428L, "fabio.capello@polito.it", "password", "Fabio", "Capello");
        addProfessor(13429L, "lorenzo.dedominicis@polito.it", "password", "Lorenzo", "De Dominicis");
        addProfessor(24393L, "leonardo.tirato@polito.it", "password", "Leonardo", "Tirato");
        addProfessor(22294L, "fabiana.canella@polito.it", "password", "Fabiana", "Canella");
    }

    public void addStudent(Long id, String email, String password, String firstName, String lastName) {
        String username = "s" + id;
        Student student = new Student(id, email, firstName, lastName, false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        User user = new User(id, username, email, passwordEncoder.encode(password), true, new Timestamp(System.currentTimeMillis()), null, List.of("ROLE_STUDENT"));
        userRepository.save(user);
        studentRepository.save(student);
    }

    public void enrollStudent(Long studentId, String courseCode) {
        Student student = studentRepository.getOne(studentId);
        Course course = courseRepository.getOne(courseCode);
        course.addStudent(student);
    }

    public void addStudents() {
        addStudent(248530L, "s248530@studenti.polito.it", "password", "Francesco", "Mantovani");
        addStudent(251129L, "s251129@studenti.polito.it", "password", "Martina", "Preta");
        addStudent(259434L, "s259434@studenti.polito.it", "password", "Sara", "Bergamini");
        addStudent(218506L, "s218506@studenti.polito.it", "password", "Davide", "Sottilini");
        addStudent(266394L, "s266394@studenti.polito.it", "password", "Filippo", "Preziosi");
        addStudent(250354L, "s250354@studenti.polito.it", "password", "Giorgia", "Trevisan");
        addStudent(243044L, "s243044@studenti.polito.it", "password", "Deborah", "De Luigi");
        addStudent(210293L, "s210293@studenti.polito.it", "password", "Renzo", "D'Ottavio");
        addStudent(240394L, "s240394@studenti.polito.it", "password", "Alessandro", "Sottile");
        addStudent(209341L, "s209341@studenti.polito.it", "password", "Fabiana", "Rai");
        addStudent(245458L, "s245458@studenti.polito.it", "password", "Francesca", "Popoli");
        addStudent(254390L, "s254390@studenti.polito.it", "password", "Vittoria", "De Franco");
        addStudent(202123L, "s202123@studenti.polito.it", "password", "Francesco", "De Carlo");
        addStudent(234943L, "s234943@studenti.polito.it", "password", "Martina", "Prezzi");
        addStudent(229302L, "s229302@studenti.polito.it", "password", "Alessandro", "Trentino");
    }

    public void enrollStudents() {
        enrollStudent(248530L, "01QYDOV"); enrollStudent(248530L, "02DUCOV");
        enrollStudent(251129L, "01NYHOV");
        enrollStudent(218506L, "01SQNOV"); enrollStudent(218506L, "02JGROV"); enrollStudent(218506L, "02DUCOV");
        enrollStudent(266394L, "01SQNOV");
        enrollStudent(243044L, "01QYDOV"); enrollStudent(243044L, "01SQNOV");
        enrollStudent(210293L, "01NYHOV"); enrollStudent(210293L, "02DUCOV");
        enrollStudent(240394L, "02JGROV");
        enrollStudent(245458L, "01QYDOV"); enrollStudent(245458L, "02JGROV"); enrollStudent(245458L, "01SQNOV");
        enrollStudent(254390L, "01QYDOV"); enrollStudent(254390L, "02JGROV");
        enrollStudent(202123L, "02DUCOV");
        enrollStudent(234943L, "01NYHOV"); enrollStudent(234943L, "01SQNOV"); enrollStudent(234943L, "02DUCOV");
        enrollStudent(229302L, "01NYHOV");
    }

    public void addCourse(String code, String name, String acronym, Integer min, Integer max, Boolean enabled) {
        Course course = new Course(code, name, acronym, min, max, enabled, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null);
        courseRepository.save(course);
    }

    public void profToCourse(String courseCode, Long professorId) {
        Course course = courseRepository.getOne(courseCode);
        Professor professor = professorRepository.getOne(professorId);
        course.addProfessor(professor);
    }

    public void addCourses() {
        addCourse("01NYHOV", "Applicazioni Internet", "AI", 2, 4, true);
        addCourse("01QYDOV", "Information systems", "IS", 1, 7, true);
        addCourse("01SQNOV", "Data spaces", "DS", 3, 5, true);
        addCourse("02JGROV", "Computer system security", "CSS", 2, 5, false);
        addCourse("02DUCOV", "Software Engineering II", "SE2", 3, 6, true);
    }

    public void addProfessorsToCourses() {
        profToCourse("01NYHOV", 14724L);
        profToCourse("01QYDOV", 25842L);
        profToCourse("01SQNOV", 19428L);
        profToCourse("02JGROV", 24393L);
        profToCourse("02DUCOV", 22294L);
    }

    public void addTeam(String teamName, String courseCode, List<Long> studentIds) {
        Course course = courseRepository.getOne(courseCode);
        Team team = Team.builder()
                .name(teamName)
                .formationStatus(Team.FormationStatus.COMPLETE)
                .invitationsExpiration(new Timestamp(System.currentTimeMillis()))
                .lastAction(new Timestamp(System.currentTimeMillis()))
                .course(course)
                .build();
        teamRepository.save(team);
        List<Student> students = studentRepository.findAllById(studentIds);
        List<TeamStudent> members = students.stream().map(s -> new TeamStudent(s, team, students.indexOf(s) == 0 ?  TeamStudent.InvitationStatus.CREATOR : TeamStudent.InvitationStatus.ACCEPTED))
                .collect(Collectors.toList());
        members.forEach(m -> teamStudentRepository.save(m));
    }

    public void addTeams() {
        addTeam("team-1", "01NYHOV", List.of(251129L, 229302L));
        addTeam("team-alpha", "02DUCOV", List.of(202123L, 210293L, 248530L));
    }

    public void addVmModel(String name, String configuration, String courseCode) {
        Course course = courseRepository.getOne(courseCode);
        VmModel vmModel = new VmModel(null, name, configuration, null);
        vmModelRepository.save(vmModel);
        course.setVmModel(vmModel);
    }

    public void addTeamVmLimit(Integer vcpus, Integer diskSpace, Integer ram, Integer instances, Integer activeInstances, Long teamId) {
        Team team = teamRepository.getOne(teamId);
        TeamVmsResources teamVmsResources = new TeamVmsResources(vcpus, diskSpace, ram, instances, activeInstances);
        team.setVmsResourcesLimits(teamVmsResources);
        teamRepository.save(team);
    }

    public void addTeamVm(Integer vcpus, Integer diskSpace, Integer ram, Long teamId) {
        Team team = teamRepository.getOne(teamId);
        Vm vm = Vm.builder()
                .team(team)
                .online(false)
                .vcpus(vcpus)
                .ram(ram)
                .diskSpace(diskSpace)
                .creator(team.getMembers().get(0).getStudent())
                .build();
        vm.addOwner(team.getMembers().get(0).getStudent());
        vmRepository.save(vm);

    }

    public void addProfessorProfilePic(Long professorId) {
        Professor professor = professorRepository.getOne(professorId);
        File file = new File("mock_profile_pictures/professor/" + professorId + ".jpg");
        try {
            InputStream stream = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), MediaType.IMAGE_JPEG_VALUE, stream);
            profilePicturesUtility.postProfilePicture(professorId, ProfilePicturesUtility.ProfileType.PROFESSOR, multipartFile);
            professor.setHasPicture(true);
            professorRepository.save(professor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addStudentProfilePic(Long studentId) {
        Student student = studentRepository.getOne(studentId);
        File file = new File("mock_profile_pictures/student/" + studentId + ".jpg");
        try {
            InputStream stream = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), MediaType.IMAGE_JPEG_VALUE, stream);
            profilePicturesUtility.postProfilePicture(studentId, ProfilePicturesUtility.ProfileType.STUDENT, multipartFile);
            student.setHasPicture(true);
            studentRepository.save(student);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addVmModels() {
        addVmModel("Spring+Angular", "FROM ubuntu", "01NYHOV");
        addVmModel("Spring+React", "FROM kubuntu", "02DUCOV");
    }

    public void addTeamVmLimits() {
        addTeamVmLimit(5, 30, 3000, 5, 3, studentRepository.getOne(202123L).getTeams().stream().filter(t -> t.getTeam().getCourse().getCode().equals("02DUCOV")).findFirst().get().getTeam().getId());
    }

    public void addTeamVms() {
        addTeamVm(2, 10, 1000, studentRepository.getOne(202123L).getTeams().stream().filter(t -> t.getTeam().getCourse().getCode().equals("02DUCOV")).findFirst().get().getTeam().getId());
    }

    public void addProfessorProfilePics() {
        addProfessorProfilePic(14724L);
        addProfessorProfilePic(18329L);
        addProfessorProfilePic(25842L);
        addProfessorProfilePic(19428L);
        addProfessorProfilePic(13429L);
        addProfessorProfilePic(24393L);
        addProfessorProfilePic(22294L);

    }

    public void addStudentProfilePics() {
        addStudentProfilePic(248530L);
        addStudentProfilePic(251129L);
        addStudentProfilePic(259434L);
        addStudentProfilePic(218506L);
        addStudentProfilePic(266394L);
        addStudentProfilePic(250354L);
        addStudentProfilePic(243044L);
        addStudentProfilePic(210293L);
        addStudentProfilePic(240394L);
        addStudentProfilePic(209341L);
        addStudentProfilePic(245458L);
        addStudentProfilePic(254390L);
        addStudentProfilePic(202123L);
        addStudentProfilePic(234943L);
        addStudentProfilePic(229302L);
    }

    public void insertMockData() {
        addProfessors();
        addStudents();
        addCourses();

        addProfessorsToCourses();
        enrollStudents();
        addTeams();
        addVmModels();
        addTeamVmLimits();
        addTeamVms();
        addProfessorProfilePics();
        addStudentProfilePics();
    }

}

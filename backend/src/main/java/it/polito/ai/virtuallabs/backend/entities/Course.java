package it.polito.ai.virtuallabs.backend.entities;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    private String code;

    private String name;

    private String acronym;

    private Integer minTeamMembers;

    private Integer maxTeamMembers;

    private Boolean enabled;

    @Builder.Default
    @ManyToMany(mappedBy = "courses", cascade = CascadeType.MERGE)
    private List<Student> students = new ArrayList<>();

    @Builder.Default
    @ManyToMany(mappedBy = "courses", cascade = CascadeType.MERGE)
    private List<Professor> professors = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "course")
    private List<Team> teams = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Homework> homeworks = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vmModel_id", referencedColumnName = "id")
    private VmModel vmModel;

    public void addHomework(Homework h) {
        this.homeworks.add(h);
        h.setCourse(this);
    }

    public void addStudent(Student s) {
        this.students.add(s);
        s.getCourses().add(this);
    }

    public void removeStudent(Student s) {
        this.students.remove(s);
        s.getCourses().remove(this);
    }

    public void addProfessor(Professor p) {
        this.professors.add(p);
        p.getCourses().add(this);
    }

    public void removeAllProfessors() {
        this.professors.forEach(p -> p.getCourses().remove(this));
        this.professors.clear();
    }

    public void removeAllStudents(){
        this.students.forEach(s -> s.getCourses().remove(this));
        this.students.clear();
    }

    public void removeAllTeams(){
        this.teams.forEach(Team::unsetCourse);
        this.teams.clear();
    }

    public void setVmModel(VmModel vmModel) {
        this.vmModel = vmModel;
        vmModel.setCourse(this);
    }
    
}

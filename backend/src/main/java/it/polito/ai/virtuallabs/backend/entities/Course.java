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
    @ManyToMany(mappedBy = "courses", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)//Aggiunt MERGE e EAGER
    private List<Student> students = new ArrayList<>();

    @Builder.Default
    @ManyToMany(mappedBy = "courses", cascade = CascadeType.MERGE)
    private List<Professor> professors = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "course")
    private List<Team> teams = new ArrayList<>();

    public void addStudent(Student s) {
        this.students.add(s);
        s.getCourses().add(this);
    }

    public void addProfessor(Professor p) {
        this.professors.add(p);
        p.getCourses().add(this);
    }

}

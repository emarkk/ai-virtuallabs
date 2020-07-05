package it.polito.ai.virtuallabs.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
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
    @ManyToMany(mappedBy = "courses")
    private List<Student> students = new ArrayList<>();

    @Builder.Default
    @ManyToMany(mappedBy = "courses")
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

package it.polito.ai.virtuallabs.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student extends AuthenticatedEntity {

    @Id
    private Long id;

    private String email;

    private String firstName;

    private String lastName;

    private String picturePath;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "student_course", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "course_code"))
    private List<Course> courses = new ArrayList<>();

    @ManyToMany(mappedBy = "members")
    private List<Team> teams = new ArrayList<>();

}

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
public class Student extends AuthenticatedEntity {

    @Id
    private Long id;

    private String email;

    private String firstName;

    private String lastName;

    private boolean hasPicture;

    private String resumedInfos;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}) //Aggiunto EAGER
    @JoinTable(name = "student_course", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "course_code"))
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private List<TeamStudent> teamStudents = new ArrayList<>();

}

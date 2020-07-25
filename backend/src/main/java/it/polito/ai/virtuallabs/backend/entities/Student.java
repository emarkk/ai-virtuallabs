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

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "student_course", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "course_code"))
    private List<Course> courses = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "owner_vm", joinColumns = @JoinColumn(name = "owner_id"), inverseJoinColumns = @JoinColumn(name = "vm_id"))
    private List<Vm> vms = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private List<TeamStudent> teams = new ArrayList<>();

}

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
public class Professor extends AuthenticatedEntity {

    @Id
    private Long id;

    private String email;

    private String firstName;

    private String lastName;

    private boolean hasPicture;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}) //Aggiunto EAGER
    @JoinTable(name = "professor_course", joinColumns = @JoinColumn(name = "professor_id"), inverseJoinColumns = @JoinColumn(name = "course_code"))
    private List<Course> courses = new ArrayList<>();

    public Boolean getHasPicture() {
        return hasPicture;
    }

}

package it.polito.ai.virtuallabs.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Homework {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Timestamp assigned;

    private Timestamp due;

    private String descriptionFilePath;

    @ManyToOne
    @JoinColumn(name = "course_code", nullable = false)
    private Course course;

    private void assignCourse(Course c) {
        this.course = c;
        c.getHomeworks().add(this);
    }

    private void removeCourse() {
        course.getHomeworks().remove(this);
        course = null;
    }

}

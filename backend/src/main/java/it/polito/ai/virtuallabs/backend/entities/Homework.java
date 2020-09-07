package it.polito.ai.virtuallabs.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Homework {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private Timestamp publicationDate;

    private Timestamp dueDate;

    @ManyToOne
    @JoinColumn(name = "course_code", nullable = false)
    private Course course;

    @Builder.Default
    @OneToMany(mappedBy = "homework")
    private List<HomeworkAction> homeworkActions = new ArrayList<>();

    private void assignCourse(Course c) {
        this.course = c;
        c.getHomeworks().add(this);
    }

    private void removeCourse() {
        this.course.getHomeworks().remove(this);
        this.course = null;
    }

}

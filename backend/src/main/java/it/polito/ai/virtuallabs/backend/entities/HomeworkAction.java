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
public class HomeworkAction {

    public enum ActionType {
        READ,
        DELIVERY,
        REVIEW,
        REVIEW_FINAL
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Timestamp date;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @ManyToOne
    @JoinColumn(name = "homework_id", nullable = false)
    private Homework homework;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    public void assignHomework(Homework h) {
        this.homework = h;
        h.getHomeworkActions().add(this);
    }

    public void removeHomework() {
        this.homework.getHomeworkActions().remove(this);
        this.homework = null;
    }

    public void assignStudent(Student s) {
        this.student = s;
        s.getHomeworkActions().add(this);
    }

    public void removeStudent() {
        this.student.getHomeworkActions().remove(this);
        this.student = null;
    }

}

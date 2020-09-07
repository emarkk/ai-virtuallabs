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
        REVIEW
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Timestamp creationDate;

    private ActionType actionType;

    @ManyToOne
    @JoinColumn(name = "homework_id", nullable = false)
    private Homework homework;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    private void assignHomework(Homework h) {
        this.homework = h;
        h.getHomeworkActions().add(this);
    }

    private void removeHomework() {
        this.homework.getHomeworkActions().remove(this);
        this.homework = null;
    }

    private void assignStudent(Student s) {
        this.student = s;
        s.getHomeworkActions().add(this);
    }

    private void removeStudent() {
        this.student.getHomeworkActions().remove(this);
        this.student = null;
    }

}

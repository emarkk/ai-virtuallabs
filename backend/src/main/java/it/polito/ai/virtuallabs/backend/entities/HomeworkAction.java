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
        NULL,
        READ,
        DELIVERY,
        REVIEW,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Timestamp date;

    private Integer mark;

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

    public void assignStudent(Student s) {
        this.student = s;
        s.getHomeworkActions().add(this);
    }

    public Boolean isDelivery() {
        return this.actionType == ActionType.DELIVERY;
    }

    public Boolean isRead() {
        return this.actionType == ActionType.READ;
    }

    public Boolean isReview() {
        return this.actionType == ActionType.REVIEW;
    }

    public Boolean isNull() {
        return this.actionType == ActionType.NULL;
    }


}

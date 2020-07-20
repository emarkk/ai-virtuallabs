package it.polito.ai.virtuallabs.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamStudent {

    public enum Status{
        creator,
        pending,
        accepted,
        rejected
    };


    @Id
    @GeneratedValue
    private Long id;



    @Enumerated(EnumType.STRING)
    private Status status;


    @JoinColumn(name = "student_id")
    @ManyToOne
    private Student student;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    public void setTeam(Team t) {
        this.team = t;
        t.getTeamStudents().add(this);
    }

    public void setStudent(Student s) {
        this.student = s;
        s.getTeamStudents().add(this);
    }

}

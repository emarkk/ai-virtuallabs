package it.polito.ai.virtuallabs.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamInvitation {
    @Id
    @GeneratedValue
    private Long id;

    private Timestamp expirationDate;

    //creator
    //pending
    //accepted
    //rejected
    private String status;

    @JoinColumn(name = "addressee_student_id")
    @ManyToOne
    private Student addresseeStudent;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    public void setTeam(Team t) {
        this.team = t;
        t.getTeamInvitations().add(this);
    }

    public void setAddresseeStudent(Student s) {
        this.addresseeStudent = s;
        s.getTeamInvitations().add(this);
    }

}

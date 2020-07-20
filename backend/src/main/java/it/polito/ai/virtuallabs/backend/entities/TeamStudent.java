package it.polito.ai.virtuallabs.backend.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Data
@Entity
@NoArgsConstructor
public class TeamStudent {

    public enum InvitationStatus {
        CREATOR,
        PENDING,
        ACCEPTED,
        REJECTED
    }

    @Embeddable
    public static class TeamStudentId implements Serializable {

        @Column(name = "student_id")
        protected Long studentId;

        @Column(name = "team_id")
        protected Long teamId;

        public TeamStudentId() {
        }
        public TeamStudentId(Long studentId, Long teamId) {
            this.studentId = studentId;
            this.teamId = teamId;
        }
        @Override
        public int hashCode() {
            return Objects.hash(this.studentId, this.teamId);
        }
        @Override
        public boolean equals(Object obj) {
            if(obj == null || getClass() != obj.getClass())
                return false;

            TeamStudentId other = (TeamStudentId) obj;
            return this.studentId.equals(other.studentId) && this.teamId.equals(other.teamId);
        }
    }

    @EmbeddedId
    private TeamStudentId id;

    @Enumerated(EnumType.STRING)
    private InvitationStatus invitationStatus;

    @ManyToOne
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "team_id", insertable = false, updatable = false)
    private Team team;

    public TeamStudent(Student student, Team team, InvitationStatus invitationStatus) {
        this.id = new TeamStudentId(student.getId(), team.getId());

        this.student = student;
        this.team = team;
        this.invitationStatus = invitationStatus;

        this.student.getTeams().add(this);
        this.team.getMembers().add(this);
    }

}

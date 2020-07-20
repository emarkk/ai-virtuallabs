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
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    public enum FormationStatus {
        PROVISIONAL,
        COMPLETE,
        ABORTED,
        EXPIRED
    }

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private FormationStatus formationStatus;

    private Timestamp invitationsExpiration;

    @ManyToOne
    @JoinColumn(name = "course_code")
    private Course course;

    @Builder.Default
    @OneToMany(mappedBy = "team")
    private List<TeamStudent> members = new ArrayList<>();

    public void setCourse(Course c) {
        this.course = c;
        c.getTeams().add(this);
    }

}
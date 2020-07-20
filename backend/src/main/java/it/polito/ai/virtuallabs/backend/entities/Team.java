package it.polito.ai.virtuallabs.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private Integer status;

    @ManyToOne
    @JoinColumn(name = "course_code")
    private Course course;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "student_team", joinColumns = @JoinColumn(name = "team_id"), inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> members = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "team")
    private List<TeamInvitation> teamInvitations = new ArrayList<>();



    public void setCourse(Course c) {
        this.course = c;
        c.getTeams().add(this);
    }

    public void addMember(Student s) {
        this.members.add(s);
        s.getTeams().add(this);
    }

    public void addTeamInvitation(TeamInvitation teamInvitation) {
        this.teamInvitations.add(teamInvitation);
        teamInvitation.setTeam(this);
    }

}
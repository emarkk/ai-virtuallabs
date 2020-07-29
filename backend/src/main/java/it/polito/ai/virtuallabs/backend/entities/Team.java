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

    private Timestamp lastAction;

    @ManyToOne
    @JoinColumn(name = "course_code")
    private Course course;

    @Builder.Default
    @OneToMany(mappedBy = "team")
    private List<TeamStudent> members = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "team")
    private List<Vm> vms = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vmConfLimit_id", referencedColumnName = "id")
    private VmConfigurationLimits vmConfigurationLimits;

    public void setCourse(Course c) {
        this.course = c;
        c.getTeams().add(this);
    }

    public void setVmConfigurationLimits(VmConfigurationLimits vmConfigurationLimits) {
        this.vmConfigurationLimits = vmConfigurationLimits;
        vmConfigurationLimits.setTeam(this);
    }

}
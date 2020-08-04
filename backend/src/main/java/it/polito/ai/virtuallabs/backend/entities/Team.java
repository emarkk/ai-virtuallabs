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
    @JoinColumn(name = "team_vms_resources_limits_id", referencedColumnName = "id")
    private TeamVmsResourcesLimits vmsResourcesLimits;

    public TeamVmsResources getVmsResourcesUsed() {
        return this.getVms().stream()
                .reduce(new TeamVmsResources(0, 0, 0, 0, 0),
                        (resources, vm) -> resources.add(TeamVmsResources.fromVm(vm)), TeamVmsResources::add);
    }

    public TeamVmsResources getVmsResourcesLimits() {
        if(this.vmsResourcesLimits != null)
            return this.vmsResourcesLimits;

        return TeamVmsResourcesLimits.DEFAULT_VMS_RESOURCES_LIMITS;
    }

    public Boolean isComplete() {
        return this.formationStatus == FormationStatus.COMPLETE;
    }

    public Boolean isProvisional() {
        return this.formationStatus == FormationStatus.PROVISIONAL;
    }

    public Boolean isActive() {
        return this.isComplete() || this.isProvisional();
    }

    public void setCourse(Course c) {
        this.course = c;
        c.getTeams().add(this);
    }

    public void setVmsResourcesLimits(TeamVmsResourcesLimits vmsResourcesLimits) {
        this.vmsResourcesLimits = vmsResourcesLimits;
        vmsResourcesLimits.setTeam(this);
    }

}
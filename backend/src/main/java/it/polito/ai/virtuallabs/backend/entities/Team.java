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
    @OneToMany(mappedBy = "team", cascade = CascadeType.REMOVE)
    private List<TeamStudent> members = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "team", cascade = CascadeType.REMOVE)
    private List<Vm> vms = new ArrayList<>();

    @Embedded
    private TeamVmsResources vmsResourcesLimits;

    public static final TeamVmsResources DEFAULT_VMS_RESOURCES_LIMITS = new TeamVmsResources(16, 256, 8192, 6, 3);

    public TeamVmsResources getVmsResourcesUsed() {
        return this.getVms().stream()
                .reduce(new TeamVmsResources(0, 0, 0, 0, 0),
                        (resources, vm) -> resources.add(TeamVmsResources.fromVm(vm)), TeamVmsResources::add);
    }

    public TeamVmsResources getVmsResourcesLimits() {
        if(this.vmsResourcesLimits != null)
            return this.vmsResourcesLimits;

        return DEFAULT_VMS_RESOURCES_LIMITS;
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

    public void unsetCourse() {
        this.course = null;
    }
}
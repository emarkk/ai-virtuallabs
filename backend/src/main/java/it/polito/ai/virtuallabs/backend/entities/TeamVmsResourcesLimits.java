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
public class TeamVmsResourcesLimits extends TeamVmsResources {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer vCpus;

    private Integer diskSpace;

    private Integer ram;

    private Integer instances;

    private Integer activeInstances;

    @OneToOne(mappedBy = "vmsResourcesLimits")
    private Team team;

    @Override
    public boolean greaterThan(TeamVmsResources other) {
        return this.vCpus >= other.getVCpus()
                && this.diskSpace >= other.getDiskSpace()
                && this.ram >= other.getRam()
                && this.instances >= other.getInstances()
                && this.activeInstances >= other.getActiveInstances();
    }

    public static final TeamVmsResourcesLimits DEFAULT_VMS_RESOURCES_LIMITS = new TeamVmsResourcesLimits(null, 16, 256, 8192, 6, 3, null);

}

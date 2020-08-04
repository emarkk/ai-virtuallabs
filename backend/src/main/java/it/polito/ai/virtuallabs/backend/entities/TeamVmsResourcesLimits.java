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

    public static final TeamVmsResources DEFAULT_VMS_RESOURCES_LIMITS = new TeamVmsResources(16, 256, 8192, 6, 3);

}

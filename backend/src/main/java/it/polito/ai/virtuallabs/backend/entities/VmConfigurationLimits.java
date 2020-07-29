package it.polito.ai.virtuallabs.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VmConfigurationLimits {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer vCpus;

    private Integer diskSpace;

    private Integer ram;

    private Integer maxInstances;

    private Integer maxActiveInstances;

    @OneToOne(mappedBy = "vmConfigurationLimits")
    private Team team;

    public static final VmConfigurationLimits defaultVmLimits = new VmConfigurationLimits(null, 8, 40, 4096, 10, 8, null);

}

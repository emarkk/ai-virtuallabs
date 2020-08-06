package it.polito.ai.virtuallabs.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Data
@Embeddable@NoArgsConstructor
@AllArgsConstructor
public class TeamVmsResources {

    private Integer vcpus;

    private Integer diskSpace;

    private Integer ram;

    private Integer instances;

    private Integer activeInstances;

    public static TeamVmsResources fromVm(Vm vm) {
        return new TeamVmsResources(vm.getVcpus(), vm.getDiskSpace(), vm.getRam(), 1, vm.getOnline() ? 1 : 0);
    }

    public TeamVmsResources add(TeamVmsResources other) {
        return new TeamVmsResources(
                this.vcpus + other.getVcpus(),
                this.diskSpace + other.getDiskSpace(),
                this.ram + other.getRam(),
                this.instances + other.getInstances(),
                this.activeInstances + other.getActiveInstances()
        );
    }

    public boolean greaterThan(TeamVmsResources other) {
        return this.vcpus >= other.getVcpus()
                && this.diskSpace >= other.getDiskSpace()
                && this.ram >= other.getRam()
                && this.instances >= other.getInstances()
                && this.activeInstances >= other.getActiveInstances();
    }

}

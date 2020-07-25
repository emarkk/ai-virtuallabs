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
public class Vm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer vCpus;

    private Long diskSpace;

    private Long ram;

    private Boolean online;

    @Builder.Default
    @ManyToMany(mappedBy = "vms", cascade = CascadeType.MERGE)
    private List<Student> owners = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    public void setTeam(Team t) {
        this.team = t;
        t.getVms().add(this);
    }

    public void addOwner(Student s) {
        this.owners.add(s);
        s.getVms().add(this);
    }

    public void removeOwner(Student s) {
        this.owners.remove(s);
        s.getVms().remove(this);
    }

    public void removeAllOwners(){
        this.owners.forEach(s -> s.getVms().remove(this));
        this.owners.clear();
    }

}

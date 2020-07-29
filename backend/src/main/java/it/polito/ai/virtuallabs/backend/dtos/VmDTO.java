package it.polito.ai.virtuallabs.backend.dtos;

import it.polito.ai.virtuallabs.backend.entities.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VmDTO {

    private Long id;

    private Integer vCpus;

    private Integer diskSpace;

    private Integer ram;

    private Boolean online;

    private List<Long> owners;

    public void setOwners(List<Student> students) {
        this.owners = students.stream().map(Student::getId).collect(Collectors.toList());
    }
}

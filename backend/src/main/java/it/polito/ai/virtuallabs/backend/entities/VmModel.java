package it.polito.ai.virtuallabs.backend.entities;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "course")
public class VmModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String configuration;

    @OneToOne(mappedBy = "vmModel")
    private Course course;
}

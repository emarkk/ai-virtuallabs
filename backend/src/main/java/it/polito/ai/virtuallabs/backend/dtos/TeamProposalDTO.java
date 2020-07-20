package it.polito.ai.virtuallabs.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TeamProposalDTO {
    private String courseCode;
    private String name;
    private int timeout;
    private List<Long> membersIds;
}

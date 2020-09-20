package it.polito.ai.virtuallabs.backend.websockets.signals;

import it.polito.ai.virtuallabs.backend.dtos.ProfessorDTO;
import it.polito.ai.virtuallabs.backend.dtos.StudentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.*;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class VmScreenSignal {

    private Boolean online;

    private String teamName;

    private List<ProfessorDTO> connectedProfessors;

    private List<StudentDTO> connectedStudents;

}

package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.VmModelDTO;

public interface VmService {
    VmModelDTO addVmModel(String courseCode, String name, String configuration);
    VmModelDTO updateVmModel(Long vmModelId, VmModelDTO vmModelDTO);
}

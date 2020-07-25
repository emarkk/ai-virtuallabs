package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.VmDTO;
import it.polito.ai.virtuallabs.backend.dtos.VmModelDTO;

import java.util.List;

public interface VmService {
    VmModelDTO addVmModel(String courseCode, String name, String configuration);
    VmModelDTO updateVmModel(Long vmModelId, VmModelDTO vmModelDTO);
    VmModelDTO getVmModel(Long vmModelId);
    VmDTO addVm(Long teamId, Integer vCpus, Long diskSpace, Long ram);
    List<Boolean> addVmOwners(Long vmId, List<Long> studentIds);
}

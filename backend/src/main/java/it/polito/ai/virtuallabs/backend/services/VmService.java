package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.VmDTO;
import it.polito.ai.virtuallabs.backend.dtos.VmModelDTO;

import java.util.List;

public interface VmService {
    VmModelDTO addVmModel(String courseCode, String name, String configuration);
    VmModelDTO updateVmModel(Long vmModelId, VmModelDTO vmModelDTO);
    VmModelDTO getVmModel(Long vmModelId);
    VmDTO addVm(Long teamId, Integer vcpus, Integer diskSpace, Integer ram);
    List<Boolean> addVmOwners(Long vmId, List<Long> studentIds);
    VmDTO getVm(Long vmId);
    VmDTO updateVm(Long vmId, Integer vcpus, Integer diskSpace, Integer ram);
    void deleteVm(Long vmId);
    void turnOnVm(Long vmId);
    void turnOffVm(Long vmId);
    Boolean studentHasSignalPermission(Long vmId, Long studentId);
    Boolean professorHasSignalPermission(Long vmId, Long professorId);
}

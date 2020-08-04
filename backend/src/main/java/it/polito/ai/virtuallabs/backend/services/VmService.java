package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.VmConfigurationLimitsDTO;
import it.polito.ai.virtuallabs.backend.dtos.VmDTO;
import it.polito.ai.virtuallabs.backend.dtos.VmModelDTO;

import java.util.List;

public interface VmService {
    VmModelDTO addVmModel(String courseCode, String name, String configuration);
    VmModelDTO updateVmModel(Long vmModelId, VmModelDTO vmModelDTO);
    VmModelDTO getVmModel(Long vmModelId);
    VmDTO addVm(Long teamId, Integer vCpus, Integer diskSpace, Integer ram);
    List<Boolean> addVmOwners(Long vmId, List<Long> studentIds);
    VmDTO getVm(Long vmId);
    VmDTO updateVm(Long vmId, Integer vCpus, Integer diskSpace, Integer ram);
    void deleteVm(Long vmId);
    void turnOnVm(Long vmId);
    void turnOffVm(Long vmId);
    byte[] connectVm(Long vmId);
    VmConfigurationLimitsDTO addVmConfigurationLimit(Long teamId, Integer maxVCpus, Integer maxDiskSpace, Integer maxRam, Integer maxInstances, Integer maxActiveInstances);
    VmConfigurationLimitsDTO getVmConfigurationLimits(Long vmConfigurationLimitsId);
    VmConfigurationLimitsDTO updateVmConfigurationLimits(Long vmConfigurationLimitsId, Integer maxVCpus, Integer maxDiskSpace, Integer maxRam, Integer maxInstances, Integer maxActiveInstances);
    Boolean studentHasSignalPermission(Long vmId, Long studentId);
    Boolean professorHasSignalPermission(Long vmId, Long professorId);
}

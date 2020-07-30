package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.VmConfigurationLimitsDTO;
import it.polito.ai.virtuallabs.backend.dtos.VmDTO;
import it.polito.ai.virtuallabs.backend.dtos.VmModelDTO;
import it.polito.ai.virtuallabs.backend.entities.*;
import it.polito.ai.virtuallabs.backend.repositories.*;
import it.polito.ai.virtuallabs.backend.security.AuthenticatedEntityMapper;
import it.polito.ai.virtuallabs.backend.utils.GetterProxy;
import it.polito.ai.virtuallabs.backend.utils.VmConnectionUtility;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class VmServiceImpl implements VmService {

    @Autowired
    private VmRepository vmRepository;

    @Autowired
    private VmConfigurationLimitsRepository vmConfigurationLimitsRepository;

    @Autowired
    private VmModelRepository vmModelRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private AuthenticatedEntityMapper authenticatedEntityMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GetterProxy getter;

    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    @Override
    public VmModelDTO addVmModel(String courseCode, String name, String configuration) {

        Course course = getter.course(courseCode);

        if(!course.getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        if(course.getVmModel() != null)
            throw new VmModelAlreadyExistsException();

        VmModel vmModel = VmModel.builder()
                .name(name)
                .configuration(configuration)
                .course(course)
                .build();
        vmModelRepository.save(vmModel);
        course.setVmModel(vmModel);
        courseRepository.save(course);

        return modelMapper.map(vmModel, VmModelDTO.class);
    }

    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    @Override
    public VmModelDTO updateVmModel(Long vmModelId, VmModelDTO vmModelDTO) {

        VmModel vmModel = getter.vmModel(vmModelId);

        if(!vmModel.getCourse().getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        vmModel.setName(vmModelDTO.getName());
        vmModel.setConfiguration(vmModelDTO.getConfiguration());
        vmModelRepository.save(vmModel);

        return modelMapper.map(vmModel, VmModelDTO.class);
    }

    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    @Override
    public VmModelDTO getVmModel(Long vmModelId) {
        VmModel vmModel = getter.vmModel(vmModelId);
        if(!vmModel.getCourse().getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        return modelMapper.map(vmModel, VmModelDTO.class);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Override
    public VmDTO addVm(Long teamId, Integer vCpus, Integer diskSpace, Integer ram) {
        Team team = getter.team(teamId);

        if(!team.getFormationStatus().equals(Team.FormationStatus.COMPLETE))
            throw new TeamNotActiveException();

        Student authenticated = (Student) authenticatedEntityMapper.get();

        if(!team.getMembers().stream().map(TeamStudent::getStudent).collect(Collectors.toList()).contains(authenticated))
            throw new StudentNotInTeamException();

        if(vCpus < 0 || diskSpace < 0 || ram < 0)
            throw new IllegalVmConfigurationException();

        //Check dei limits
        VmConfigurationLimits limits = team.getVmConfigurationLimits();
        if(limits == null)
            limits = VmConfigurationLimits.defaultVmLimits;
        Vm vmTotalResources = team.getVms().stream()
                .reduce(Vm.builder().vCpus(0).diskSpace(0).ram(0).online(false).build(), (vmPartial, vm) -> Vm.builder()
                        .vCpus(vmPartial.getVCpus() + vm.getVCpus())
                        .diskSpace(vmPartial.getDiskSpace() + vm.getDiskSpace())
                        .ram(vmPartial.getRam() + vm.getRam())
                        .build());

        if(vmTotalResources.getVCpus() + vCpus > limits.getVCpus() || vmTotalResources.getDiskSpace() + diskSpace > limits.getDiskSpace() || vmTotalResources.getRam() + ram > limits.getRam())
            throw new IllegalVmConfigurationException();
        if(team.getVms().size() + 1 > limits.getMaxInstances())
            throw new VmInstancesLimitNumberException();

        Vm vm = Vm.builder()
                .diskSpace(diskSpace)
                .vCpus(vCpus)
                .ram(ram)
                .online(false)
                .build();
        vm.setTeam(team);
        vm.addOwner(authenticated);
        vmRepository.save(vm);
        return modelMapper.map(vm, VmDTO.class);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Override
    public List<Boolean> addVmOwners(Long vmId, List<Long> studentIds) {
        Vm vm = getter.vm(vmId);
        if(vm.getOnline())
            throw new VmOnlineException();
        HashSet hashSet = new HashSet(studentIds);
        if(hashSet.size() < studentIds.size())
            throw new DuplicateParticipantException();
        if(!vm.getOwners().contains((Student) authenticatedEntityMapper.get()))
            throw new IllegalVmOwnerException();
        List<Boolean> result = studentIds
                .stream()
                .map(id -> {
                    Student s = getter.student(id);
                    if(!vm.getTeam().getMembers().stream().map(TeamStudent::getStudent).collect(Collectors.toList()).contains(s))
                        throw new StudentNotInTeamException();
                    if(vm.getOwners().contains(s))
                        return false;
                    vm.addOwner(s);
                    return true;
                })
                .collect(Collectors.toList());
        vmRepository.save(vm);
        return result;
    }

    @Override
    public VmDTO getVm(Long vmId) {
        Vm vm = getter.vm(vmId);
        AuthenticatedEntity authenticatedEntity = authenticatedEntityMapper.get();

        if(authenticatedEntity.getClass().equals(Professor.class) && !((Professor) authenticatedEntity).getCourses().contains(vm.getTeam().getCourse()))
            throw new NotAllowedException();
        if(authenticatedEntity.getClass().equals(Student.class) && !((Student) authenticatedEntity).getTeams().stream().map(TeamStudent::getTeam).collect(Collectors.toList()).contains(vm.getTeam()))
            throw new StudentNotInTeamException();
        return modelMapper.map(vm, VmDTO.class);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Override
    public void deleteVm(Long vmId) {
        Vm vm = getter.vm(vmId);
        if(!vm.getOwners().contains((Student) authenticatedEntityMapper.get()))
            throw new IllegalVmOwnerException();
        if(vm.getOnline())
            throw new VmOnlineException();
        vm.removeAllOwners();
        vmRepository.delete(vm);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Override
    public void turnOnVm(Long vmId) {
        Vm vm = getter.vm(vmId);
        if(!vm.getOwners().contains((Student) authenticatedEntityMapper.get()))
            throw new IllegalVmOwnerException();
        VmConfigurationLimits limits = vm.getTeam().getVmConfigurationLimits();
        if(limits == null)
            limits = VmConfigurationLimits.defaultVmLimits;
        if(((int) vm.getTeam().getVms().stream().filter(Vm::getOnline).count()) + 1 > limits.getMaxActiveInstances())
            throw new VmActiveInstancesLimitNumberException();
        vm.setOnline(true);
        vmRepository.save(vm);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Override
    public void turnOffVm(Long vmId) {
        Vm vm = getter.vm(vmId);
        if(!vm.getOwners().contains((Student) authenticatedEntityMapper.get()))
            throw new IllegalVmOwnerException();
        vm.setOnline(false);
        vmRepository.save(vm);
    }

    @Override
    public byte[] connectVm(Long vmId) {
        Vm vm = getter.vm(vmId);

        AuthenticatedEntity authenticatedEntity = authenticatedEntityMapper.get();

        if(authenticatedEntity.getClass().equals(Professor.class) && !((Professor) authenticatedEntity).getCourses().contains(vm.getTeam().getCourse()))
            throw new NotAllowedException();
        if(authenticatedEntity.getClass().equals(Student.class) && !((Student) authenticatedEntity).getTeams().stream().map(TeamStudent::getTeam).collect(Collectors.toList()).contains(vm.getTeam()))
            throw new StudentNotInTeamException();
        if(!vm.getOnline())
            throw new VmOfflineException();

        return VmConnectionUtility.retrieveVm();

    }

    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    @Override
    public VmConfigurationLimitsDTO addVmConfigurationLimit(Long teamId, Integer vCpus, Integer diskSpace, Integer ram, Integer maxInstances, Integer maxActiveInstances) {
        Team team = getter.team(teamId);
        if(!team.getFormationStatus().equals(Team.FormationStatus.COMPLETE))
            throw new TeamNotActiveException();
        if(!team.getCourse().getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();
        if(team.getVmConfigurationLimits() != null)
            throw new VmConfigurationLimitsAlreadyExistsException();

        Vm vmTotalResources = team.getVms().stream()
                .reduce(Vm.builder().vCpus(0).diskSpace(0).ram(0).online(false).build(), (vmPartial, vm) -> Vm.builder()
                        .vCpus(vmPartial.getVCpus() + vm.getVCpus())
                        .diskSpace(vmPartial.getDiskSpace() + vm.getDiskSpace())
                        .ram(vmPartial.getRam() + vm.getRam())
                        .build());
        if(vCpus < vmTotalResources.getVCpus() || diskSpace < vmTotalResources.getDiskSpace() || ram < vmTotalResources.getRam() || maxInstances < team.getVms().size() || maxActiveInstances < ((int) team.getVms().stream().filter(Vm::getOnline).count()) || maxInstances < maxActiveInstances)
            throw new IllegalVmConfigurationLimitsException();

        VmConfigurationLimits limits = VmConfigurationLimits.builder()
                .vCpus(vCpus)
                .diskSpace(diskSpace)
                .ram(ram)
                .maxInstances(maxInstances)
                .maxActiveInstances(maxActiveInstances)
                .team(team)
                .build();
        vmConfigurationLimitsRepository.save(limits);
        team.setVmConfigurationLimits(limits);
        teamRepository.save(team);
        return modelMapper.map(limits, VmConfigurationLimitsDTO.class);
    }

    @Override
    public VmConfigurationLimitsDTO getVmConfigurationLimits(Long vmConfigurationLimitsId) {
        VmConfigurationLimits vmConfigurationLimits = getter.vmConfigurationLimits(vmConfigurationLimitsId);

        AuthenticatedEntity authenticatedEntity = authenticatedEntityMapper.get();
        if(authenticatedEntity.getClass().equals(Professor.class) && !((Professor) authenticatedEntity).getCourses().contains(vmConfigurationLimits.getTeam().getCourse()))
            throw new NotAllowedException();
        if(authenticatedEntity.getClass().equals(Student.class) && !((Student) authenticatedEntity).getTeams().stream().map(TeamStudent::getTeam).collect(Collectors.toList()).contains(vmConfigurationLimits.getTeam()))
            throw new StudentNotInTeamException();

        return modelMapper.map(vmConfigurationLimits, VmConfigurationLimitsDTO.class);
    }
}


package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.VmDTO;
import it.polito.ai.virtuallabs.backend.dtos.VmModelDTO;
import it.polito.ai.virtuallabs.backend.entities.*;
import it.polito.ai.virtuallabs.backend.repositories.*;
import it.polito.ai.virtuallabs.backend.security.AuthenticatedEntityMapper;
import it.polito.ai.virtuallabs.backend.utils.GetterProxy;
import it.polito.ai.virtuallabs.backend.websockets.SignalService;
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
    private VmModelRepository vmModelRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AuthenticatedEntityMapper authenticatedEntityMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GetterProxy getter;

    @Autowired
    private SignalService signalService;

    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    @Override
    public VmModelDTO getVmModel(Long vmModelId) {
        VmModel vmModel = getter.vmModel(vmModelId);

        if(!vmModel.getCourse().getProfessors().contains((Professor) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        return modelMapper.map(vmModel, VmModelDTO.class);
    }

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

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Override
    public VmDTO addVm(Long teamId, Integer vcpus, Integer diskSpace, Integer ram) {
        Team team = getter.team(teamId);
        Student authenticated = (Student) authenticatedEntityMapper.get();

        // team must be active
        if(!team.isComplete())
            throw new TeamNotActiveException();
        // requesting student must be part of the team
        if(!team.getMembers().stream().map(TeamStudent::getStudent).collect(Collectors.toList()).contains(authenticated))
            throw new StudentNotInTeamException();
        if(vcpus < 0 || diskSpace < 0 || ram < 0)
            throw new IllegalVmConfigurationException();

        Vm vm = Vm.builder()
                .diskSpace(diskSpace)
                .vcpus(vcpus)
                .ram(ram)
                .online(false)
                .build();
        vm.setTeam(team);
        vm.addOwner(authenticated);
        vm.setCreator(authenticated);

        if(!team.getVmsResourcesLimits().greaterThan(team.getVmsResourcesUsed()))
            throw new TeamVmsResourcesLimitsExceededException();

        vmRepository.save(vm);
        signalService.vmCreated(vm);

        return modelMapper.map(vm, VmDTO.class);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Override
    public List<Boolean> addVmOwners(Long vmId, List<Long> studentIds) {
        Vm vm = getter.vm(vmId);
        HashSet<Long> hashSet = new HashSet<>(studentIds);

        if(vm.getOnline())
            throw new VmOnlineException();
        if(hashSet.size() < studentIds.size())
            throw new DuplicateParticipantException();
        if(!vm.getOwners().contains((Student) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        List<Boolean> result = studentIds
                .stream()
                .map(id -> {
                    Student s = getter.student(id);
                    // student must be part of the team
                    if(!vm.getTeam().getMembers().stream().map(TeamStudent::getStudent).collect(Collectors.toList()).contains(s))
                        throw new NotAllowedException();
                    if(vm.getOwners().contains(s))
                        return false;
                    vm.addOwner(s);
                    return true;
                })
                .collect(Collectors.toList());

        vmRepository.save(vm);
        signalService.vmUpdated(vm);

        return result;
    }

    @Override
    public VmDTO getVm(Long vmId) {
        Vm vm = getter.vm(vmId);
        AuthenticatedEntity authenticatedEntity = authenticatedEntityMapper.get();

        if(authenticatedEntity instanceof Student && ((Student) authenticatedEntity).getTeams().stream().noneMatch(ts -> ts.getTeam().equals(vm.getTeam())))
            throw new NotAllowedException();
        if(authenticatedEntity instanceof Professor && !((Professor) authenticatedEntity).getCourses().contains(vm.getTeam().getCourse()))
            throw new NotAllowedException();
        if(!vm.getTeam().isComplete())
            throw new TeamNotActiveException();

        return modelMapper.map(vm, VmDTO.class);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Override
    public VmDTO updateVm(Long vmId, Integer vcpus, Integer diskSpace, Integer ram) {
        Vm vm = getter.vm(vmId);

        if(vm.getOnline())
            throw new VmOnlineException();
        if(!((Student) authenticatedEntityMapper.get()).getTeams().stream().map(TeamStudent::getTeam).collect(Collectors.toList()).contains(vm.getTeam()))
            throw new StudentNotInTeamException();
        if(vcpus < 0 || diskSpace < 0 || ram < 0)
            throw new IllegalVmConfigurationException();

        vm.setVcpus(vcpus);
        vm.setDiskSpace(diskSpace);
        vm.setRam(ram);

        if(!vm.getTeam().getVmsResourcesLimits().greaterThan(vm.getTeam().getVmsResourcesUsed()))
            throw new TeamVmsResourcesLimitsExceededException();

        vmRepository.save(vm);
        signalService.vmUpdated(vm);

        return modelMapper.map(vm, VmDTO.class);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Override
    public void deleteVm(Long vmId) {
        Vm vm = getter.vm(vmId);

        if(!vm.getOwners().contains((Student) authenticatedEntityMapper.get()))
            throw new NotAllowedException();
        if(vm.getOnline())
            throw new VmOnlineException();

        vm.getTeam().getVms().remove(vm);
        vm.removeAllOwners();
        vmRepository.delete(vm);
        signalService.vmDeleted(vm);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Override
    public void turnOnVm(Long vmId) {
        Vm vm = getter.vm(vmId);

        if(!vm.getOwners().contains((Student) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        vm.setOnline(true);

        if(!vm.getTeam().getVmsResourcesLimits().greaterThan(vm.getTeam().getVmsResourcesUsed()))
            throw new TeamVmsResourcesLimitsExceededException();

        vmRepository.save(vm);
        signalService.vmUpdated(vm);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Override
    public void turnOffVm(Long vmId) {
        Vm vm = getter.vm(vmId);

        if(!vm.getOwners().contains((Student) authenticatedEntityMapper.get()))
            throw new NotAllowedException();

        vm.setOnline(false);

        vmRepository.save(vm);
        signalService.vmUpdated(vm);
    }

    @Override
    public Boolean studentHasSignalPermission(Long vmId, Long studentId) {
        Vm vm = getter.vm(vmId);
        Student student = getter.student(studentId);

        return vm.getTeam().getMembers().stream().anyMatch(ts -> ts.getStudent() == student);
    }

    @Override
    public Boolean professorHasSignalPermission(Long vmId, Long professorId) {
        Vm vm = getter.vm(vmId);
        Professor professor = getter.professor(professorId);

        return vm.getTeam().getCourse().getProfessors().contains(professor);
    }
}


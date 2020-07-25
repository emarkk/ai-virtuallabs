package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.VmDTO;
import it.polito.ai.virtuallabs.backend.dtos.VmModelDTO;
import it.polito.ai.virtuallabs.backend.entities.*;
import it.polito.ai.virtuallabs.backend.repositories.CourseRepository;
import it.polito.ai.virtuallabs.backend.repositories.StudentRepository;
import it.polito.ai.virtuallabs.backend.repositories.VmModelRepository;
import it.polito.ai.virtuallabs.backend.repositories.VmRepository;
import it.polito.ai.virtuallabs.backend.security.AuthenticatedEntityMapper;
import it.polito.ai.virtuallabs.backend.utils.GetterProxy;
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
    VmRepository vmRepository;

    @Autowired
    VmModelRepository vmModelRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    AuthenticatedEntityMapper authenticatedEntityMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    GetterProxy getter;

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
    public VmDTO addVm(Long teamId, Integer vCpus, Long diskSpace, Long ram) {
        Team team = getter.team(teamId);

        if(!team.getFormationStatus().equals(Team.FormationStatus.COMPLETE))
            throw new TeamNotActiveException();

        Student authenticated = (Student) authenticatedEntityMapper.get();

        if(!team.getMembers().stream().map(TeamStudent::getStudent).collect(Collectors.toList()).contains(authenticated))
            throw new StudentNotInTeamException();

        if(vCpus < 0 || diskSpace < 0 || ram < 0)
            throw new IllegalVmConfigurationException();

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

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Override
    public void deleteVm(Long vmId) {
        Vm vm = getter.vm(vmId);
        if(!vm.getOwners().contains((Student) authenticatedEntityMapper.get()))
            throw new IllegalVmOwnerException();
        vm.removeAllOwners();
        vmRepository.delete(vm);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Override
    public void turnOnVm(Long vmId) {
        Vm vm = getter.vm(vmId);
        if(!vm.getOwners().contains((Student) authenticatedEntityMapper.get()))
            throw new IllegalVmOwnerException();
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
}

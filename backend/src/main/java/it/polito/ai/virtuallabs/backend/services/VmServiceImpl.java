package it.polito.ai.virtuallabs.backend.services;

import it.polito.ai.virtuallabs.backend.dtos.VmModelDTO;
import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Professor;
import it.polito.ai.virtuallabs.backend.entities.VmModel;
import it.polito.ai.virtuallabs.backend.repositories.CourseRepository;
import it.polito.ai.virtuallabs.backend.repositories.VmModelRepository;
import it.polito.ai.virtuallabs.backend.security.AuthenticatedEntityMapper;
import it.polito.ai.virtuallabs.backend.utils.GetterProxy;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class VmServiceImpl implements VmService {

    @Autowired
    VmModelRepository vmModelRepository;

    @Autowired
    CourseRepository courseRepository;

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
}

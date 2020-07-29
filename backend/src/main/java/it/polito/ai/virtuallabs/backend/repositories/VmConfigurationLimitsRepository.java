package it.polito.ai.virtuallabs.backend.repositories;

import it.polito.ai.virtuallabs.backend.entities.VmConfigurationLimits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VmConfigurationLimitsRepository extends JpaRepository<VmConfigurationLimits, Long> {
}

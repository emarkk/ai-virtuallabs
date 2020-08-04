package it.polito.ai.virtuallabs.backend.repositories;

import it.polito.ai.virtuallabs.backend.entities.TeamVmsResourcesLimits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamVmsResourcesLimitsRepository extends JpaRepository<TeamVmsResourcesLimits, Long> {
}

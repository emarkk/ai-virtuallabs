package it.polito.ai.virtuallabs.backend.repositories;

import it.polito.ai.virtuallabs.backend.entities.TeamStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamStudentRepository extends JpaRepository<TeamStudent, Long> {
    List<TeamStudent> findAllByTeamId(Long teamId);
}
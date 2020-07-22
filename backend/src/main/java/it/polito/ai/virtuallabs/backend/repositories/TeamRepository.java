package it.polito.ai.virtuallabs.backend.repositories;

import it.polito.ai.virtuallabs.backend.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findAllByFormationStatusIsAndInvitationsExpirationIsBetween(Team.FormationStatus status, Timestamp t1, Timestamp t2);
    List<Team> findAllByFormationStatusIsAndInvitationsExpirationIsBefore(Team.FormationStatus status, Timestamp t);
    List<Team> findAllByFormationStatusIsAndLastActionIsBefore(Team.FormationStatus status, Timestamp t);
}

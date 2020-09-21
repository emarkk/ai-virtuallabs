package it.polito.ai.virtuallabs.backend.repositories;

import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findAllByFormationStatusIsAndInvitationsExpirationIsBetween(Team.FormationStatus status, Timestamp t1, Timestamp t2);

    @Transactional
    void deleteAllByFormationStatusIsAndInvitationsExpirationIsBefore(Team.FormationStatus status, Timestamp t);

    @Transactional
    void deleteAllByFormationStatusIsAndLastActionIsBefore(Team.FormationStatus status, Timestamp t);
}

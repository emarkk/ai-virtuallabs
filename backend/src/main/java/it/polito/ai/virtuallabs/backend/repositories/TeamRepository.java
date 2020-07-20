package it.polito.ai.virtuallabs.backend.repositories;

import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Student;
import it.polito.ai.virtuallabs.backend.entities.Team;
import it.polito.ai.virtuallabs.backend.entities.TeamStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Team findByNameAndCourse(String teamName, Course course);

    @Query("SELECT ts FROM TeamStudent ts INNER JOIN ts.student s INNER JOIN ts.team t INNER JOIN t.course c WHERE c.code = :courseCode AND s.id = :id AND t.status = :status")
    List<Team> findTeamsByCourseAndStatus(String courseCode, Long id, Integer status);
}

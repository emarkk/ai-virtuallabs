package it.polito.ai.virtuallabs.backend.repositories;

import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

    @Query("SELECT s FROM Student s INNER JOIN s.teamStudents ts INNER JOIN ts.team t INNER JOIN t.course c WHERE c.name = :courseName")
    List<Student> getStudentsInTeams(String courseName);

    @Query("SELECT s FROM Student s INNER JOIN s.courses c WHERE c.name = :courseName AND s.id NOT IN (SELECT s.id FROM Student s INNER JOIN s.teamStudents ts INNER JOIN ts.team t INNER JOIN t.course c WHERE c.name = :courseName)")
    List<Student> getStudentsNotInTeams(String courseName);

}

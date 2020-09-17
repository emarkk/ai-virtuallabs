package it.polito.ai.virtuallabs.backend.repositories;

import it.polito.ai.virtuallabs.backend.dtos.CourseStudentDTO;
import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
    List<Student> findAll(Specification<Student> filters);

    @Query("SELECT s AS student, t as team, t.name as teamName FROM Student s LEFT OUTER JOIN s.teams ts ON (ts.invitationStatus = 'CREATOR' OR ts.invitationStatus = 'ACCEPTED') LEFT OUTER JOIN ts.team t ON ((t.formationStatus = 'PROVISIONAL' OR t.formationStatus = 'COMPLETE') AND t.course = :course) WHERE :course MEMBER OF s.courses")
    Page<CourseStudent> findAllWithTeamByCoursesIsContaining(Pageable pageable, Course course);
}

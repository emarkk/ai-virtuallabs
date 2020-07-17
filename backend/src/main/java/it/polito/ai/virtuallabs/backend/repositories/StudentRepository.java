package it.polito.ai.virtuallabs.backend.repositories;

import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> getByResumedInfosContaining(String q);
    List<Student> getByResumedInfosContainingAndCoursesIsNotContaining(String q, Course course);
    //Il secondo Course è quello contenuto
    List<Student> getByResumedInfosContainingAndCoursesIsNotContainingAndCoursesIsContaining(String q, Course c1, Course c2);
    List<Student> getByResumedInfosContainingAndCoursesIsContaining(String q, Course course);
    Page<Student> findAllByCoursesIsContaining(Pageable pageable, Course course);

}

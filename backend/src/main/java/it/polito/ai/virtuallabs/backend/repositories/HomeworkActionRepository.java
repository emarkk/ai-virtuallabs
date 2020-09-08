package it.polito.ai.virtuallabs.backend.repositories;

import it.polito.ai.virtuallabs.backend.entities.Homework;
import it.polito.ai.virtuallabs.backend.entities.HomeworkAction;
import it.polito.ai.virtuallabs.backend.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HomeworkActionRepository extends JpaRepository<HomeworkAction, Long> {
    Optional<HomeworkAction> findByHomeworkAndStudent(Homework h, Student s);
}

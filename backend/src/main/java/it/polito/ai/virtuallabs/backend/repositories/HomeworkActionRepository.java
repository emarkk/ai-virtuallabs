package it.polito.ai.virtuallabs.backend.repositories;

import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Homework;
import it.polito.ai.virtuallabs.backend.entities.HomeworkAction;
import it.polito.ai.virtuallabs.backend.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HomeworkActionRepository extends JpaRepository<HomeworkAction, Long> {
    Optional<HomeworkAction> findByHomeworkAndStudent(Homework h, Student s);

    @Query("SELECT ha FROM HomeworkAction ha INNER JOIN ha.homework h INNER JOIN ha.student s WHERE h.id = :homeworkId AND ha.id IN (SELECT ha1.id FROM HomeworkAction ha1 INNER JOIN ha1.student s1 WHERE s1.id = s.id AND ha1.date = (SELECT MAX(ha2.date) FROM HomeworkAction ha2 INNER JOIN ha2.student s2 WHERE s2.id = s1.id))")
    Page<HomeworkAction> findAllActions(Pageable pageable, @Param("homeworkId") Long homeworkId);

    @Query("SELECT ha FROM HomeworkAction ha INNER JOIN ha.homework h INNER JOIN ha.student s WHERE h.id = :homeworkId AND ha.actionType = :actionType AND ha.id IN (SELECT ha1.id FROM HomeworkAction ha1 INNER JOIN ha1.student s1 WHERE s1.id = s.id AND ha1.date = (SELECT MAX(ha2.date) FROM HomeworkAction ha2 INNER JOIN ha2.student s2 WHERE s2.id = s1.id))")
    Page<HomeworkAction> findAllByHomeworkIdAndActionType(Pageable pageable, @Param("homeworkId") Long homeworkId, @Param("actionType") HomeworkAction.ActionType actionType);
}

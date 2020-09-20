package it.polito.ai.virtuallabs.backend.repositories.specifications;

import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Student;
import it.polito.ai.virtuallabs.backend.entities.Team;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.Collection;
import java.util.List;

public class StudentSpecifications {

    public static Specification<Student> enrolledInCourse(Course c) {
        return (Specification<Student>) (student, query, builder) -> {
            final Path<Collection<Course>> courses = student.get("courses");
            return builder.isMember(c, courses);
        };
    }

    public static Specification<Student> notEnrolledInCourse(Course c) {
        return (Specification<Student>) (student, query, builder) -> {
            final Path<Collection<Course>> courses = student.get("courses");
            return builder.isNotMember(c, courses);
        };
    }

    public static Specification<Student> excludeIds(List<Long> ids) {
        return (Specification<Student>) (student, query, builder) -> {
            final Path<Long> id = student.get("id");
            return builder.not(id.in(ids));
        };
    }
}

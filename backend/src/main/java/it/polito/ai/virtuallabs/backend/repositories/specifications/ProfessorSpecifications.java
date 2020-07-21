package it.polito.ai.virtuallabs.backend.repositories.specifications;

import it.polito.ai.virtuallabs.backend.entities.Course;
import it.polito.ai.virtuallabs.backend.entities.Professor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;
import java.util.Collection;

public class ProfessorSpecifications {

    public static Specification<Professor> notTeachingCourse(Course c) {
        return (Specification<Professor>) (professor, query, builder) -> {
            final Path<Collection<Course>> courses = professor.get("courses");
            return builder.isNotMember(c, courses);
        };
    }

}

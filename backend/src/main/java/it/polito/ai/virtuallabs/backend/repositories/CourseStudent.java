package it.polito.ai.virtuallabs.backend.repositories;

import it.polito.ai.virtuallabs.backend.entities.Student;
import it.polito.ai.virtuallabs.backend.entities.Team;

public interface CourseStudent {
    Student getStudent();
    Team getTeam();
    String getTeamName();
}

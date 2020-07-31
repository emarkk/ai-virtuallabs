import { Student } from './student.model';
import { Team } from './team.model';

export class EnrolledStudent extends Student {
  team: Team;

  constructor(id: number, firstName: string, lastName: string, email: string, hasPicture: boolean, team: Team) {
    super(id, firstName, lastName, email, hasPicture);
    this.team = team;
  }
}
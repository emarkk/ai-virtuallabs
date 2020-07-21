import { Student } from './student.model';

export enum TeamStatus {
  PROVISIONAL,
  COMPLETE,
  ABORTED
};

export enum TeamInvitationStatus {
  CREATOR,
  PENDING,
  ACCEPTED,
  DECLINED
};

export class Team {
  id: number;
  name: string;
  status: TeamStatus;
  members: Map<Student, TeamInvitationStatus>;

  constructor(id: number, name: string, status: TeamStatus, members: Map<Student, TeamInvitationStatus>) {
    this.id = id;
    this.name = name;
    this.status = status;
    this.members = members;
  }
}
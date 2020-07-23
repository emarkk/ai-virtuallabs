import { Student } from './student.model';

export enum TeamStatus {
  PROVISIONAL = 'PROVISIONAL',
  COMPLETE = 'COMPLETE',
  ABORTED = 'ABORTED',
  EXPIRED = 'EXPIRED'
};

export enum TeamInvitationStatus {
  CREATOR = 'CREATOR',
  PENDING = 'PENDING',
  ACCEPTED = 'ACCEPTED',
  DECLINED = 'DECLINED'
};

export type TeamMember = { student: Student, status: TeamInvitationStatus };

export class Team {
  id: number;
  name: string;
  status: TeamStatus;
  members: TeamMember[];
  invitationsExpiration: Date;
  lastAction: Date;

  constructor(id: number, name: string, status: TeamStatus, members: TeamMember[], invitationsExpiration: Date, lastAction: Date) {
    this.id = id;
    this.name = name;
    this.status = status;
    this.members = members;
    this.invitationsExpiration = invitationsExpiration;
    this.lastAction = lastAction;
  }
  getMemberStatus(studentId) {
    const member = this.members.find(m => m.student.id == studentId);
    return member ? member.status : null;
  }
}
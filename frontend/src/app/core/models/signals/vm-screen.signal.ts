import { Professor } from '../professor.model';
import { Student } from '../student.model';

export class VmScreenSignal {
  online: boolean;
  teamName: string;
  connectedProfessors: Professor[];
  connectedStudents: Student[];
  mousePos: any;

  constructor(online: boolean, teamName: string, connectedProfessors: Professor[], connectedStudents: Student[], mousePos: any) {
    this.online = online;
    this.teamName = teamName;
    this.connectedProfessors = connectedProfessors;
    this.connectedStudents = connectedStudents;
    this.mousePos = mousePos;
  }
  static fromMsg(msg: any): VmScreenSignal {
    return new VmScreenSignal(
      msg.online,
      msg.teamName,
      msg.connectedProfessors ? msg.connectedProfessors.map(p => new Professor(p.id, p.firstName, p.lastName, p.email, p.hasPicture)) : null,
      msg.connectedProfessors ? msg.connectedStudents.map(s => new Student(s.id, s.firstName, s.lastName, s.email, s.hasPicture)) : null,
      msg.mousePos
    );
  }
};
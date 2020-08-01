import { Student } from './student.model';

export class Vm {
  id: number;
  vCpus: number;
  diskSpace: number;
  ram: number;
  online: boolean;
  ownersIds: number[];
  creator: Student;

  constructor(id: number, vCpus: number, diskSpace: number, ram: number, online: boolean, ownersIds: number[], creator: Student) {
    this.id = id;
    this.vCpus = vCpus;
    this.diskSpace = diskSpace;
    this.ram = ram;
    this.online = online;
    this.ownersIds = ownersIds;
    this.creator = creator;
  }
}
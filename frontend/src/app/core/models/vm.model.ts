export class Vm {
  id: number;
  vCpus: number;
  diskSpace: number;
  ram: number;
  online: boolean;
  ownersIds: number[];

  constructor(id: number, vCpus: number, diskSpace: number, ram: number, online: boolean, ownersIds: number[]) {
    this.id = id;
    this.vCpus = vCpus;
    this.diskSpace = diskSpace;
    this.ram = ram;
    this.online = online;
    this.ownersIds = ownersIds;
  }
}
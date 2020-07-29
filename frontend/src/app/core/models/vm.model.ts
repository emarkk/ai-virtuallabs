export class Vm {
  id: number;
  vCpus: number;
  diskSpace: number;
  ram: number;
  online: boolean;
  owners: number[];

  constructor(id: number, vCpus: number, diskSpace: number, ram: number, online: boolean, owners: number[]) {
    this.id = id;
    this.vCpus = vCpus;
    this.diskSpace = diskSpace;
    this.ram = ram;
    this.online = online;
    this.owners = owners;
  }
}
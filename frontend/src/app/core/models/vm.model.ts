export class Vm {
  id: number;
  vCpus: number;
  diskSpace: number;
  ram: number;
  online: boolean;

  constructor(id: number, vCpus: number, diskSpace: number, ram: number, online: boolean) {
    this.id = id;
    this.vCpus = vCpus;
    this.diskSpace = diskSpace;
    this.ram = ram;
    this.online = online;
  }
}
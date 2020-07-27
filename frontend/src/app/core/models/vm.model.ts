export class Vm {
  id: number;
  vCpus: number;
  diskSpace: number;
  ram: number;

  constructor(id: number, vCpus: number, diskSpace: number, ram: number) {
    this.id = id;
    this.vCpus = vCpus;
    this.diskSpace = diskSpace;
    this.ram = ram;
  }
}
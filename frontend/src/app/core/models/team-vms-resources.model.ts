export class TeamVmsResources {
  vCpus: number;
  diskSpace: number;
  ram: number;
  instances: number;
  activeInstances: number;

  constructor(vCpus: number, diskSpace: number, ram: number, instances: number, activeInstances: number) {
    this.vCpus = vCpus;
    this.diskSpace = diskSpace;
    this.ram = ram;
    this.instances = instances;
    this.activeInstances = activeInstances;
  }
}
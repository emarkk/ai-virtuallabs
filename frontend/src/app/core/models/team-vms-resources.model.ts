export class TeamVmsResources {
  vcpus: number;
  diskSpace: number;
  ram: number;
  instances: number;
  activeInstances: number;

  constructor(vcpus: number, diskSpace: number, ram: number, instances: number, activeInstances: number) {
    this.vcpus = vcpus;
    this.diskSpace = diskSpace;
    this.ram = ram;
    this.instances = instances;
    this.activeInstances = activeInstances;
  }
}
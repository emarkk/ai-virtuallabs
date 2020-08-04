import { Vm } from '../vm.model';
import { Student } from '../student.model';

export enum VmSignalUpdateType {
  CREATED = 'CREATED',
  UPDATED = 'UPDATED',
  DELETED = 'DELETED'
};

export class VmSignal {
  vm: Vm;
  updateType: VmSignalUpdateType;

  constructor(vm: Vm, updateType: VmSignalUpdateType) {
    this.vm = vm;
    this.updateType = updateType;
  }
  static fromMsg(msg: any) {
    return new VmSignal(
      new Vm(msg.vm.id, msg.vm.vcpus, msg.vm.disk, msg.vm.ram, msg.vm.online, msg.vm.owners,
        new Student(msg.vm.creator.id, msg.vm.creator.firstName, msg.vm.creator.lastName, msg.vm.creator.email, msg.vm.creator.hasPicture)),
      msg.updateType as VmSignalUpdateType);
  }
}
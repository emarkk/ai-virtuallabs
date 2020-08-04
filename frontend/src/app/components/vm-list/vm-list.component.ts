import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

import { Vm } from 'src/app/core/models/vm.model';
import { TeamVmsResources } from 'src/app/core/models/team-vms-resources.model';

@Component({
  selector: 'app-vm-list',
  templateUrl: './vm-list.component.html',
  styleUrls: ['./vm-list.component.css']
})
export class VmListComponent implements OnInit {
  vmList: Vm[] = null;
  vmsLimits: TeamVmsResources = null;

  @Input() set vms(data: Vm[]) {
    this.vmList = data;
  }

  @Input() set limits(data: TeamVmsResources) {
    this.vmsLimits = data;
  }

  @Output() vmOnline = new EventEmitter<{ vmId: number, online: boolean }>();
  @Output() vmConnect = new EventEmitter<number>();
  @Output() vmEdit = new EventEmitter<number>();
  @Output() vmAddOwners = new EventEmitter<number>();
  @Output() vmDelete = new EventEmitter<number>();

  constructor() {
  }

  ngOnInit(): void {
  }

  getResourcesUsage() {
    return {
      instances: { available: this.vmsLimits.instances - this.vmList.length, total: this.vmsLimits.instances },
      vCpus: { available: this.vmsLimits.vCpus - this.vmList.map(vm => vm.vCpus).reduce((a, b) => a + b), total: this.vmsLimits.vCpus },
      diskSpace: { available: this.vmsLimits.diskSpace - this.vmList.map(vm => vm.diskSpace).reduce((a, b) => a + b), total: this.vmsLimits.diskSpace },
      ram: { available: this.vmsLimits.ram - this.vmList.map(vm => vm.ram).reduce((a, b) => a + b), total: this.vmsLimits.ram },
      activeInstances: { available: this.vmsLimits.activeInstances - this.vmList.filter(vm => vm.online).length, total: this.vmsLimits.activeInstances },
    };
  }
  vmStateChanged(vmId: number, online: boolean) {
    this.vmOnline.emit({ vmId, online });
  }
  vmConnected(vmId: number) {
    this.vmConnect.emit(vmId);
  }
  vmEdited(vmId: number) {
    this.vmEdit.emit(vmId);
  }
  vmAddedOwners(vmId: number) {
    this.vmAddOwners.emit(vmId);
  }
  vmDeleted(vmId: number) {
    this.vmDelete.emit(vmId);
  }
}
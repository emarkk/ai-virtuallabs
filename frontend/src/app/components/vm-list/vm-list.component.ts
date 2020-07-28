import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

import { Vm } from 'src/app/core/models/vm.model';

@Component({
  selector: 'app-vm-list',
  templateUrl: './vm-list.component.html',
  styleUrls: ['./vm-list.component.css']
})
export class VmListComponent implements OnInit {
  vmList: Vm[] = null;

  @Input() set vms(data: Vm[]) {
    this.vmList = data;
  }

  @Output() vmOnline = new EventEmitter<{ vmId: number, online: boolean }>();
  @Output() vmAddOwners = new EventEmitter<number>();
  @Output() vmDelete = new EventEmitter<number>();

  constructor() {
  }

  ngOnInit(): void {
  }

  vmStateChanged(vmId: number, online: boolean) {
    this.vmOnline.emit({ vmId, online });
  }
  vmAddedOwners(vmId: number) {
    this.vmAddOwners.emit(vmId);
  }
  vmDeleted(vmId: number) {
    this.vmDelete.emit(vmId);
  }
}
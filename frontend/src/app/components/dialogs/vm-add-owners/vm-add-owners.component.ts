import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

import { Student } from 'src/app/core/models/student.model';

import { VmService } from 'src/app/core/services/vm.service';

export interface VmAddOwnersDialogData {
  vmId: number,
  students: Student[],
  ownersIds: number[]
}

@Component({
  selector: 'app-dialog-vm-add-owners',
  templateUrl: './vm-add-owners.component.html',
  styleUrls: ['./vm-add-owners.component.css']
})
export class VmAddOwnersDialog implements OnInit {
  locked: boolean = false;
  checkedStudents = new Set<number>();
  
  constructor(public dialogRef: MatDialogRef<VmAddOwnersDialog>, @Inject(MAT_DIALOG_DATA) public data: VmAddOwnersDialogData, private vmService: VmService) {}

  ngOnInit(): void {
  }

  lock() {
    this.locked = true;
  }
  unlock() {
    this.locked = false;
  }
  setCheckedState(id: number, checked: boolean) {
    checked ? this.checkedStudents.add(id) : this.checkedStudents.delete(id);
  }
  saveButtonClicked() {
    if(this.locked)
      return;

    this.lock();
    this.vmService.addOwners(this.data.vmId, [...this.checkedStudents].filter(s => !this.data.ownersIds.includes(s))).subscribe(res => {
      this.unlock();
      this.dialogRef.close(res);
    });
  }
}
import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

export interface VmLimitsDialogData {
  teamName: string
}

@Component({
  selector: 'app-dialog-vm-limits',
  templateUrl: './vm-limits.component.html',
  styleUrls: ['./vm-limits.component.css']
})
export class VmLimitsDialog implements OnInit {
  // whether it is possible to edit the form or not
  locked: boolean = false;
  
  constructor(public dialogRef: MatDialogRef<VmLimitsDialog>, @Inject(MAT_DIALOG_DATA) public data: VmLimitsDialogData) {}

  ngOnInit(): void {
  }

}
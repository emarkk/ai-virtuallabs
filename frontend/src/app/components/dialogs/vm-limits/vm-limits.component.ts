import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormGroup, FormControl, Validators } from '@angular/forms';

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
  // form fields for vm limits editing
  form = new FormGroup({
    maxVcpu: new FormControl({ value: '', disabled: false }, [Validators.required]),
    maxDisk: new FormControl({ value: '', disabled: false }, [Validators.required]),
    maxRam: new FormControl({ value: '', disabled: false }, [Validators.required]),
    maxActiveInstances: new FormControl({ value: '', disabled: false }, [Validators.required]),
    maxInstances: new FormControl({ value: '', disabled: false }, [Validators.required])
  });
  
  constructor(public dialogRef: MatDialogRef<VmLimitsDialog>, @Inject(MAT_DIALOG_DATA) public data: VmLimitsDialogData) {}

  ngOnInit(): void {
  }

  getMaxVcpuErrorMessage() {

  }
  getMaxDiskErrorMessage() {
    
  }
  getMaxRamErrorMessage() {
    
  }
  getMaxActiveInstancesErrorMessage() {

  }
  getMaxInstancesErrorMessage() {
    
  }
}
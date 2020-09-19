import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Observable } from 'rxjs';

import { APIResult } from 'src/app/core/models/api-result.model';
import { TeamVmsResources } from 'src/app/core/models/team-vms-resources.model';

import { TeamService } from 'src/app/core/services/team.service';

import { numberValidator } from 'src/app/core/validators/core.validator';

export interface VmLimitsDialogData {
  teamId: number,
  teamName: string,
  resourcesUsed$: Observable<TeamVmsResources>,
  resourcesLimits$: Observable<TeamVmsResources>
}

@Component({
  selector: 'app-dialog-vm-limits',
  templateUrl: './vm-limits.component.html',
  styleUrls: ['./vm-limits.component.css']
})
export class VmLimitsDialog implements OnInit {
  // resources used by team
  resourcesUsed: TeamVmsResources;

  // whether it is possible to edit the form or not
  locked: boolean = false;
  // form fields for vm limits editing
  form = new FormGroup({
    maxVcpu: new FormControl({ value: '', disabled: false }, [Validators.required, numberValidator]),
    maxDisk: new FormControl({ value: '', disabled: false }, [Validators.required, numberValidator]),
    maxRam: new FormControl({ value: '', disabled: false }, [Validators.required, numberValidator]),
    maxActiveInstances: new FormControl({ value: '', disabled: false }, [Validators.required, numberValidator]),
    maxInstances: new FormControl({ value: '', disabled: false }, [Validators.required, numberValidator])
  });
  
  constructor(public dialogRef: MatDialogRef<VmLimitsDialog>, @Inject(MAT_DIALOG_DATA) public data: VmLimitsDialogData, private teamService: TeamService) {}

  ngOnInit(): void {
    this.data.resourcesUsed$.subscribe(used => {
      this.resourcesUsed = used;
      this.form.get('maxVcpu').setValidators([Validators.required, numberValidator, Validators.min(used.vcpus)]);
      this.form.get('maxDisk').setValidators([Validators.required, numberValidator, Validators.min(used.diskSpace)]);
      this.form.get('maxRam').setValidators([Validators.required, numberValidator, Validators.min(used.ram)]);
      this.form.get('maxActiveInstances').setValidators([Validators.required, numberValidator, Validators.min(used.activeInstances)]);
      this.form.get('maxInstances').setValidators([Validators.required, numberValidator, Validators.min(used.instances)]);
      
      this.form.get('maxVcpu').updateValueAndValidity();
      this.form.get('maxDisk').updateValueAndValidity();
      this.form.get('maxRam').updateValueAndValidity();
      this.form.get('maxActiveInstances').updateValueAndValidity();
      this.form.get('maxInstances').updateValueAndValidity();
    });

    this.data.resourcesLimits$.subscribe(limits => {
      this.form.get('maxVcpu').setValue(limits.vcpus);
      this.form.get('maxDisk').setValue(limits.diskSpace);
      this.form.get('maxRam').setValue(limits.ram);
      this.form.get('maxActiveInstances').setValue(limits.activeInstances);
      this.form.get('maxInstances').setValue(limits.instances);
    });
  }

  getMaxVcpuErrorMessage() {
    if(this.form.get('maxVcpu').hasError('required'))
      return 'You must enter the number of virtual CPUs';
    if(this.form.get('maxVcpu').hasError('number'))
      return 'Please enter a number here';
    if(this.form.get('maxVcpu').hasError('min'))
      return 'This value cannot be lower than currently used';
  }
  getMaxDiskErrorMessage() {
    if(this.form.get('maxDisk').hasError('required'))
      return 'You must enter the number of virtual CPUs';
    if(this.form.get('maxDisk').hasError('number'))
      return 'Please enter a number here';
    if(this.form.get('maxDisk').hasError('min'))
      return 'This value cannot be lower than currently used';
  }
  getMaxRamErrorMessage() {
    if(this.form.get('maxRam').hasError('required'))
      return 'You must enter the number of virtual CPUs';
    if(this.form.get('maxRam').hasError('number'))
      return 'Please enter a number here';
    if(this.form.get('maxRam').hasError('min'))
      return 'This value cannot be lower than currently used';
  }
  getMaxActiveInstancesErrorMessage() {
    if(this.form.get('maxActiveInstances').hasError('required'))
      return 'You must enter the number of virtual CPUs';
    if(this.form.get('maxActiveInstances').hasError('number'))
      return 'Please enter a number here';
    if(this.form.get('maxActiveInstances').hasError('min'))
      return 'This value cannot be lower than currently active';
  }
  getMaxInstancesErrorMessage() {
    if(this.form.get('maxInstances').hasError('required'))
      return 'You must enter the number of virtual CPUs';
    if(this.form.get('maxInstances').hasError('number'))
      return 'Please enter a number here';
    if(this.form.get('maxInstances').hasError('min'))
      return 'This value cannot be lower than currently used';
  }
  lock() {
    this.locked = true;
    this.form.disable();
  }
  unlock() {
    this.locked = false;
    this.form.enable();
  }
  saveButtonClicked() {
    // if form is invalid or locked, ignore
    if(this.form.invalid || this.locked)
      return;
    
    // collect form data
    const vcpus = +this.form.get('maxVcpu').value;
    const diskSpace = +this.form.get('maxDisk').value;
    const ram = +this.form.get('maxRam').value;
    const activeInstances = +this.form.get('maxActiveInstances').value;
    const instances = +this.form.get('maxInstances').value;

    this.lock();
    this.teamService.updateVmsResourcesLimits(this.data.teamId, vcpus, diskSpace, ram, activeInstances, instances).subscribe((res: APIResult) => {
      this.unlock();
      this.dialogRef.close(res);
    });
  }
}
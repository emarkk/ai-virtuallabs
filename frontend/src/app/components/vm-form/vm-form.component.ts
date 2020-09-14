import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Observable, combineLatest, BehaviorSubject } from 'rxjs';
import { switchMap, filter } from 'rxjs/operators';

import { Vm } from 'src/app/core/models/vm.model';
import { VmModel } from 'src/app/core/models/vmmodel.model';
import { TeamVmsResources } from 'src/app/core/models/team-vms-resources.model';

import { numberValidator } from '../../core/validators/core.validator';

@Component({
  selector: 'app-vm-form',
  templateUrl: './vm-form.component.html',
  styleUrls: ['./vm-form.component.css']
})
export class VmFormComponent implements OnInit {
  // link to go to when cancel button is clicked
  cancelButtonLink: string;
  // available vms resources
  resourcesAvailable: TeamVmsResources;
  // updates on vm and on resources used/limits
  updates = new BehaviorSubject<{ vm: Observable<Vm>, resourcesUsed: Observable<TeamVmsResources>, resourcesLimits: Observable<TeamVmsResources> }>(null);

  // whether it is possible to edit the form or not
  locked: boolean = false;
  // form fields for vm create/edit
  form = new FormGroup({
    model: new FormControl({ value: '', disabled: true }, [Validators.required]),
    vcpu: new FormControl({ value: '', disabled: false }, [Validators.required, numberValidator, Validators.min(1)]),
    disk: new FormControl({ value: '', disabled: false }, [Validators.required, numberValidator, Validators.min(1)]),
    ram: new FormControl({ value: '', disabled: false }, [Validators.required, numberValidator, Validators.min(1)]),
  });

  // used to pass vm model
  @Input() set model(value: VmModel) {
    if(value)
      this.form.get('model').setValue(value.name);
  }
  // used to pass vms info (vm updates, resources used and limits)
  @Input() set vmInfo$(value: { vm: Observable<Vm>, resourcesUsed: Observable<TeamVmsResources>, resourcesLimits: Observable<TeamVmsResources> }) {
    this.updates.next(value);
  }
  // used to set cancel link
  @Input() set cancelLink(value: string) {
    this.cancelButtonLink = value;
  }

  // when save button is clicked, emits course information
  @Output() update = new EventEmitter<object>();

  constructor() {
  }

  ngOnInit(): void {
    this.updates.pipe(
      filter(value => !!value),
      switchMap(value => combineLatest([value.vm, value.resourcesUsed, value.resourcesLimits]))
    ).subscribe(([vm, used, limits]) => {
      if(vm) {
        this.form.get('vcpu').setValue(vm.vcpus);
        this.form.get('disk').setValue(vm.diskSpace);
        this.form.get('ram').setValue(vm.ram);
      }
      if(used && limits) {
        this.resourcesAvailable = new TeamVmsResources(
          limits.vcpus - used.vcpus + (vm ? vm.vcpus : 0),
          limits.diskSpace - used.diskSpace + (vm ? vm.diskSpace : 0),
          limits.ram - used.ram + (vm ? vm.ram : 0),
          null,
          null
        );
        this.form.get('vcpu').setValidators([Validators.required, numberValidator, Validators.min(1), Validators.max(this.resourcesAvailable.vcpus)]);
        this.form.get('disk').setValidators([Validators.required, numberValidator, Validators.min(1), Validators.max(this.resourcesAvailable.diskSpace)]);
        this.form.get('ram').setValidators([Validators.required, numberValidator, Validators.min(1), Validators.max(this.resourcesAvailable.ram)]);
      }
      this.form.get('vcpu').updateValueAndValidity();
      this.form.get('disk').updateValueAndValidity();
      this.form.get('ram').updateValueAndValidity();
    });
  }

  getVcpuErrorMessage() {
    if(this.form.get('vcpu').hasError('required'))
      return 'You must enter the number of virtual CPUs';
    if(this.form.get('vcpu').hasError('number') || this.form.get('vcpu').hasError('min'))
      return 'Please enter a positive number here';
    if(this.form.get('vcpu').hasError('max'))
      return 'This value cannot be greater than maximum available';
  }
  getDiskErrorMessage() {
    if(this.form.get('disk').hasError('required'))
      return 'You must enter the amount of disk space';
    if(this.form.get('disk').hasError('number') || this.form.get('disk').hasError('min'))
      return 'Please enter a positive number here';
    if(this.form.get('disk').hasError('max'))
      return 'This value cannot be greater than maximum available';
  }
  getRamErrorMessage() {
    if(this.form.get('ram').hasError('required'))
      return 'You must enter the amount of RAM';
    if(this.form.get('ram').hasError('number') || this.form.get('ram').hasError('min'))
      return 'Please enter a positive number here';
    if(this.form.get('ram').hasError('max'))
      return 'This value cannot be greater than maximum available';
  }
  lock() {
    this.locked = true;
    this.form.disable();
  }
  unlock() {
    this.locked = false;
    this.form.enable();
    this.form.get('model').disable();
  }
  saveButtonClicked() {
    // if form is invalid or locked, ignore
    if(this.form.invalid || this.locked)
      return;
    
    // collect form data
    const vcpu = +this.form.get('vcpu').value;
    const disk = +this.form.get('disk').value;
    const ram = +this.form.get('ram').value;

    // emit vm information to parent
    this.update.emit({ data: { vcpu, disk, ram } });
  }
}
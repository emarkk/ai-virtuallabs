import { Component, OnInit, Input, ViewChild, EventEmitter, Output } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { Vm } from 'src/app/core/models/vm.model';
import { VmModel } from 'src/app/core/models/vmmodel.model';

import { numberValidator } from '../../core/validators/core.validator';

@Component({
  selector: 'app-vm-form',
  templateUrl: './vm-form.component.html',
  styleUrls: ['./vm-form.component.css']
})
export class VmFormComponent implements OnInit {
  // link to go to when cancel button is clicked
  cancelButtonLink: string;

  // whether it is possible to edit the form or not
  locked: boolean = false;
  // form fields for vm create/edit
  form = new FormGroup({
    model: new FormControl({ value: '', disabled: true }, [Validators.required]),
    vcpu: new FormControl({ value: '', disabled: false }, [Validators.required, numberValidator, Validators.min(1)]),
    disk: new FormControl({ value: '', disabled: false }, [Validators.required, numberValidator, Validators.min(1)]),
    ram: new FormControl({ value: '', disabled: false }, [Validators.required, numberValidator, Validators.min(1)]),
  });

  // used with vm edit, to show current vm information in the form
  @Input() set data(value: Vm) {
    console.log(value);
    this.form.get('vcpu').setValue(value.vcpus);
    this.form.get('disk').setValue(value.diskSpace);
    this.form.get('ram').setValue(value.ram);
  }
  // used to pass vm model
  @Input() set model(value: VmModel) {
    this.form.get('model').setValue(value.name);
  }

  @Input() set cancelLink(value: string) {
    this.cancelButtonLink = value;
  }

  // when save button is clicked, emits course information
  @Output() update = new EventEmitter<object>();

  constructor() {
  }

  ngOnInit(): void {
  }

  getVcpuErrorMessage() {
    if(this.form.get('vcpu').hasError('required'))
      return 'You must enter the number of virtual CPUs';
    if(this.form.get('vcpu').hasError('number') || this.form.get('vcpu').hasError('min'))
      return 'Please enter a positive number here';
  }
  getDiskErrorMessage() {
    if(this.form.get('disk').hasError('required'))
      return 'You must enter the amount of disk space';
    if(this.form.get('disk').hasError('number') || this.form.get('disk').hasError('min'))
      return 'Please enter a positive number here';
  }
  getRamErrorMessage() {
    if(this.form.get('ram').hasError('required'))
      return 'You must enter the amount of RAM';
    if(this.form.get('ram').hasError('number') || this.form.get('ram').hasError('min'))
      return 'Please enter a positive number here';
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
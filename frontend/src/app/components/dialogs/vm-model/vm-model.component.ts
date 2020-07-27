import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Observable } from 'rxjs';

import { VmService } from 'src/app/core/services/vm.service';

export interface VmModelDialogData {
  id: number,
  name: string,
  configuration: string,
  courseCode: string,
  courseName: string
}

@Component({
  selector: 'app-dialog-vm-model',
  templateUrl: './vm-model.component.html',
  styleUrls: ['./vm-model.component.css']
})
export class VmModelDialog implements OnInit {
  // fake config
  sampleConfig: string = `FROM ubuntu
RUN npm install -g @angular/cli
EXPOSE 8080
ENTRYPOINT ["nginx", "-g", "daemon off;"]`;
  
  // whether it is possible to edit the form or not
  locked: boolean = false;
  // form fields for vm model adding/update
  form = new FormGroup({
    name: new FormControl({ value: '', disabled: false }, [Validators.required]),
    config: new FormControl({ value: '', disabled: false }, [Validators.required])
  });
  
  constructor(public dialogRef: MatDialogRef<VmModelDialog>, @Inject(MAT_DIALOG_DATA) public data: VmModelDialogData, private vmService: VmService) {}

  ngOnInit(): void {
    this.form.get('name').setValue(this.data.name);
    this.form.get('config').setValue(this.data.configuration);
  }

  getNameErrorMessage() {
    if(this.form.get('name').hasError('required'))
      return 'You must enter the VM model name';
  }
  getConfigErrorMessage() {
    if(this.form.get('config').hasError('required'))
      return 'You must enter the VM model configuration';
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
    const name = this.form.get('name').value;
    const config = this.form.get('config').value;

    let request: Observable<boolean>;
    if(this.data.id)
      // request is an update to existing vm model
      request = this.vmService.updateModel(this.data.id, name, config);
    else
      // request is a creation of a new vm model
      request = this.vmService.addModel(name, config, this.data.courseCode);

    this.lock();
    request.subscribe(res => {
      this.unlock();
      this.dialogRef.close(res);
    });
  }
}
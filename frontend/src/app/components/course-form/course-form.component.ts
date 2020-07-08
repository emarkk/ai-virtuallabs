import { Component, OnInit, Input, ViewChild, EventEmitter, Output } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { MatSlideToggle } from '@angular/material/slide-toggle';

import { Course } from 'src/app/core/models/course.model';

import { numberValidator } from '../../core/validators/core.validator';
import { newCourseFormValidator } from '../../core/validators/course.validator';

@Component({
  selector: 'app-course-form',
  templateUrl: './course-form.component.html',
  styleUrls: ['./course-form.component.css']
})
export class CourseFormComponent implements OnInit {
  cancelButtonLink: string;

  locked: boolean = false;
  form = new FormGroup({
    code: new FormControl({ value: '', disabled: false }, [Validators.required]),
    name: new FormControl({ value: '', disabled: false }, [Validators.required]),
    acronym: new FormControl({ value: '', disabled: false }, [Validators.required]),
    minTeamMembers: new FormControl({ value: '', disabled: false }, [Validators.required, numberValidator, Validators.min(1)]),
    maxTeamMembers: new FormControl({ value: '', disabled: false }, [Validators.required, numberValidator, Validators.min(1)])
  }, newCourseFormValidator);

  @Input() set data(value: Course) {
    this.form.get('code').setValue(value.code);
    this.form.get('name').setValue(value.name);
    this.form.get('acronym').setValue(value.acronym);
    this.form.get('minTeamMembers').setValue(value.minTeamMembers);
    this.form.get('maxTeamMembers').setValue(value.maxTeamMembers);
  }

  @Input() set cancelLink(value: string) {
    this.cancelButtonLink = value;
  }

  @ViewChild(MatSlideToggle)
  enableSwitch: MatSlideToggle;

  @Output() update = new EventEmitter<object>();

  constructor() {
  }

  ngOnInit(): void {
  }

  getFormErrorMessage() {
    if(this.form.hasError('maxmin'))
      return 'Maximum members number should be greater than or equal to minimum number.';
  }
  getFormSubmissionErrorMessage() {
    if(this.form.hasError('error'))
      return 'An error occurred.';
  }
  getCodeErrorMessage() {
    if(this.form.get('code').hasError('required'))
      return 'You must enter the course code';
  }
  getNameErrorMessage() {
    if(this.form.get('name').hasError('required'))
      return 'You must enter the course name';
  }
  getAcronymErrorMessage() {
    if(this.form.get('acronym').hasError('required'))
      return 'You must enter the course acronym';
  }
  getMinTeamMembersErrorMessage() {
    if(this.form.get('minTeamMembers').hasError('required'))
      return 'You must enter the minimum number of team members';
    return this.form.get('minTeamMembers').hasError('number') ? 'Please enter a number here' : '';
  }
  getMaxTeamMembersErrorMessage() {
    if(this.form.get('maxTeamMembers').hasError('required'))
      return 'You must enter the maximum number of team members';
    return this.form.get('maxTeamMembers').hasError('number') ? 'Please enter a number here' : '';
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
    if(this.form.invalid || this.locked)
      return;
    
    const code = this.form.get('code').value;
    const name = this.form.get('name').value;
    const acronym = this.form.get('acronym').value;
    const minTeamMembers = this.form.get('minTeamMembers').value;
    const maxTeamMembers = this.form.get('maxTeamMembers').value;
    const enabled = this.enableSwitch.checked;

    this.update.emit({ data: { code, name, acronym, minTeamMembers, maxTeamMembers, enabled } });
  }
}
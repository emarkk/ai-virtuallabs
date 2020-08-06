import { Component, OnInit, Input, ViewChild, EventEmitter, Output } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { Course } from 'src/app/core/models/course.model';

import { numberValidator } from '../../core/validators/core.validator';
import { newCourseFormValidator } from '../../core/validators/course.validator';

@Component({
  selector: 'app-course-form',
  templateUrl: './course-form.component.html',
  styleUrls: ['./course-form.component.css']
})
export class CourseFormComponent implements OnInit {
  // link to go to when cancel button is clicked
  cancelButtonLink: string;

  // whether it is possible to edit the form or not
  locked: boolean = false;
  // form fields for course create/edit
  form = new FormGroup({
    code: new FormControl({ value: '', disabled: false }, [Validators.required]),
    name: new FormControl({ value: '', disabled: false }, [Validators.required]),
    acronym: new FormControl({ value: '', disabled: false }, [Validators.required]),
    minTeamMembers: new FormControl({ value: '', disabled: false }, [Validators.required, numberValidator, Validators.min(1)]),
    maxTeamMembers: new FormControl({ value: '', disabled: false }, [Validators.required, numberValidator, Validators.min(1)]),
    enabled: new FormControl({ value: '', disabled: false }, [Validators.required])
  }, newCourseFormValidator);

  // used with course edit, to show current course information in the form
  @Input() set data(value: Course) {
    this.form.get('code').disable();
    this.form.get('code').setValue(value.code);
    this.form.get('name').setValue(value.name);
    this.form.get('acronym').setValue(value.acronym);
    this.form.get('minTeamMembers').setValue(value.minTeamMembers);
    this.form.get('maxTeamMembers').setValue(value.maxTeamMembers);
    this.form.get('enabled').setValue(value.enabled);
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

  getFormErrorMessage() {
    if(this.form.hasError('maxmin'))
      return 'Maximum members number should be greater than or equal to minimum number.';
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
    if(this.form.get('minTeamMembers').hasError('number'))
      return 'Please enter a number here';
    return this.form.get('minTeamMembers').hasError('min') ? 'At least one team member is required' : '';
  }
  getMaxTeamMembersErrorMessage() {
    if(this.form.get('maxTeamMembers').hasError('required'))
      return 'You must enter the maximum number of team members';
    if(this.form.get('maxTeamMembers').hasError('number'))
      return 'Please enter a number here';
    return this.form.get('maxTeamMembers').hasError('min') ? 'At least one team member is required' : '';
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
    const code = this.form.get('code').value;
    const name = this.form.get('name').value;
    const acronym = this.form.get('acronym').value;
    const minTeamMembers = +this.form.get('minTeamMembers').value;
    const maxTeamMembers = +this.form.get('maxTeamMembers').value;
    const enabled = this.form.get('enabled').value;

    // emit course information to parent
    this.update.emit({ data: { code, name, acronym, minTeamMembers, maxTeamMembers, enabled } });
  }
}
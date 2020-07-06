import { Component, OnInit } from '@angular/core';
import { FormControl, Validators, FormGroup } from '@angular/forms';

import { politoEmailValidator, politoMatricolaValidator, politoSignUpFormValidator } from '../authUtils';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignUpComponent implements OnInit {
  locked: Boolean = false;
  
  form = new FormGroup({
    firstName: new FormControl({ value: '', disabled: false }, [Validators.required]),
    lastName: new FormControl({ value: '', disabled: false }, [Validators.required]),
    matricola: new FormControl({ value: '', disabled: false }, [Validators.required, politoMatricolaValidator]),
    email: new FormControl({ value: '', disabled: false }, [Validators.required, politoEmailValidator]),
    password: new FormControl({ value: '', disabled: false }, [Validators.required, Validators.minLength(6)])
  }, politoSignUpFormValidator);
  
  constructor() { }

  ngOnInit(): void {
  }
  
  getFormErrorMessage() {
    if(this.form.hasError('conflict'))
      return 'Matricola and email are inconsistent';
  }
  getFirstNameErrorMessage() {
    if(this.form.get('firstName').hasError('required'))
      return 'You must enter your first name';
  }
  getLastNameErrorMessage() {
    if(this.form.get('lastName').hasError('required'))
      return 'You must enter your last name';
  }
  getMatricolaErrorMessage() {
    if(this.form.get('matricola').hasError('required'))
      return 'You must enter your matricola';
    return this.form.get('matricola').hasError('matricola') ? 'Not a valid matricola' : '';
  }
  getEmailErrorMessage() {
    if(this.form.get('email').hasError('required'))
      return 'You must enter your PoliTo email';
    return this.form.get('email').hasError('email') ? 'Not a valid PoliTo email' : '';
  }
  getPasswordErrorMessage() {
    if(this.form.get('password').hasError('required'))
      return 'You must enter the password';
    if(this.form.get('password').hasError('minlength'))
      return 'Password should be at least 6 characters long';
  }
  lock() {
    this.locked = true;
  }
  unlock() {
    this.locked = false;
  }
  signupButtonClicked() {
  }
}

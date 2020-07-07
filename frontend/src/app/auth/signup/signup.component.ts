import { Component, OnInit } from '@angular/core';
import { FormControl, Validators, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from 'src/app/core/services/auth.service';
import { RegistrationService } from 'src/app/core/services/registration.service';
import { politoEmailValidator, politoMatricolaValidator, politoSignUpFormValidator } from '../authUtils';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignUpComponent implements OnInit {
  private locked: Boolean = false;
  private form = new FormGroup({
    firstName: new FormControl({ value: '', disabled: false }, [Validators.required]),
    lastName: new FormControl({ value: '', disabled: false }, [Validators.required]),
    matricola: new FormControl({ value: '', disabled: false }, [Validators.required, politoMatricolaValidator]),
    email: new FormControl({ value: '', disabled: false }, [Validators.required, politoEmailValidator]),
    password: new FormControl({ value: '', disabled: false }, [Validators.required, Validators.minLength(6)])
  }, politoSignUpFormValidator);
  
  constructor(private router: Router, private authService: AuthService, private registrationService: RegistrationService) {
  }

  ngOnInit(): void {
    if(this.authService.isLogged())
      this.router.navigate(['/']);
  }
  
  getFormErrorMessage() {
    if(this.form.hasError('conflict'))
      return 'Matricola and email are inconsistent';
    return this.form.hasError('error') ? 'An error occurred' : '';
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
    if(this.form.invalid || this.locked)
      return;

    this.lock();

    const firstName = this.form.get('firstName').value;
    const lastName = this.form.get('lastName').value;
    const matricola = this.form.get('matricola').value;
    const email = this.form.get('email').value;
    const password = this.form.get('password').value;

    this.registrationService.signup(firstName, lastName, matricola, email, password).subscribe(res => {
      this.unlock();
      if(res) {
        this.registrationService.setRegistrationSuccessful(true);
        this.router.navigate(['/signup/success']);
      } else {
        this.form.setErrors({ error: true });
      }
    });
  }
}

import { Component, OnInit } from '@angular/core';
import { FormControl, Validators, FormGroup } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';

import { APIResult } from 'src/app/core/models/api-result.model';

import { AuthService } from '../../core/services/auth.service';

import { politoUsernameValidator } from '../../core/validators/auth.validator';

@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.css']
})
export class SignInComponent implements OnInit {
  // optional link to redirect the user to after successful sign-in
  private redirectTo: string;

  // whether it is possible to edit the form or not
  locked: boolean = false;
  // form fields for sign-in
  form = new FormGroup({
    username: new FormControl({ value: '', disabled: false }, [Validators.required, politoUsernameValidator]),
    password: new FormControl({ value: '', disabled: false }, [Validators.required])
  });

  constructor(private router: Router, private route: ActivatedRoute, private authService: AuthService) {
  }

  ngOnInit(): void {
    // if already logged in, should not be here
    if(this.authService.isLogged())
      this.router.navigate(['/']);
    
    // set redirect param
    this.redirectTo = this.route.snapshot.params.redirect || null;
  }
  
  getUsernameErrorMessage() {
    if(this.form.get('username').hasError('required'))
      return 'You must enter a value';
    if(this.form.get('username').hasError('username'))
      return 'Not a valid PoliTo username';
  }
  getPasswordErrorMessage() {
    if(this.form.get('password').hasError('required'))
      return 'You must enter the password';
    if(this.form.get('password').hasError('wrong'))
      return 'Your email and password do not match any account';
  }
  lock() {
    this.locked = true;
    this.form.disable();
  }
  unlock() {
    this.locked = false;
    this.form.enable();
  }
  loginButtonClicked() {
    // if form is invalid or locked, ignore
    if(this.form.invalid || this.locked)
      return;

    // lock until request is completed
    this.lock();

    // login attempt
    this.authService.login(this.form.get('username').value, this.form.get('password').value).subscribe((res: APIResult) => {
      // unlock form
      this.unlock();

      if(res.ok)
        // ok; redirect to desired path or to /
        this.router.navigate([this.redirectTo || '/']);
      else
        // wrong password
        this.form.get('password').setErrors({ wrong: true });
    });
  }

}

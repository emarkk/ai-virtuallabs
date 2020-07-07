import { Component, OnInit } from '@angular/core';
import { FormControl, Validators, FormGroup } from '@angular/forms';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';
import { politoUsernameValidator } from '../authUtils';

@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.css']
})
export class SignInComponent implements OnInit {
  locked: Boolean = false;
  params: Params = null;
  
  form = new FormGroup({
    username: new FormControl({ value: '', disabled: false }, [Validators.required, politoUsernameValidator]),
    password: new FormControl({ value: '', disabled: false }, [Validators.required])
  });

  constructor(private router: Router, private route: ActivatedRoute, private authService: AuthService) {
  }

  ngOnInit(): void {
    if(this.authService.isLogged())
      this.router.navigate(['/']);
    
    this.route.queryParams.subscribe(params => {
      this.params = params;
    });
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
    this.form.get('username').disable();
    this.form.get('password').disable();
  }
  unlock() {
    this.locked = false;
    this.form.get('username').enable();
    this.form.get('password').enable();
  }
  loginButtonClicked() {
    if(this.form.invalid || this.locked)
      return;

    this.lock();
    this.authService.login(this.form.get('username').value, this.form.get('password').value).subscribe(res => {
      this.unlock();
      if(res)
        this.router.navigate([this.params.redirect || '/']);
      else
        this.form.get('password').setErrors({ wrong: true });
    });
  }

}

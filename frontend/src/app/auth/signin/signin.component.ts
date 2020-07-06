import { Component, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.css']
})
export class SignInComponent implements OnInit {
  submission: Subscription = null;
  
  username = new FormControl({ value: '', disabled: false }, [Validators.required]);
  password = new FormControl({ value: '', disabled: false }, [Validators.required]);

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
  }
  
  getUsernameErrorMessage() {
    if(this.username.hasError('required'))
      return 'You must enter a value';
  }
  getPasswordErrorMessage() {
    if(this.password.hasError('required'))
      return 'You must enter a value';
    if(this.password.hasError('wrong'))
      return 'Your email and password do not match any account.';
  }
  lock() {
    this.username.disable();
    this.password.disable();
  }
  unlock() {
    this.username.enable();
    this.password.enable();
  }
  loginButtonClicked() {
    if(this.username.invalid || this.password.invalid || this.submission)
      return;

    this.lock();
    this.submission = this.authService.login(this.username.value, this.password.value).subscribe(res => {
      this.unlock();
      if(res)
        doSomeThing();
      else
        this.password.setErrors({ wrong: true });
    });
  }

}

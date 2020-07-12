import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-container',
  templateUrl: './container.component.html',
  styleUrls: ['./container.component.css']
})
export class ContainerComponent implements OnInit {
  // whether user is logged or not
  logged: boolean;

  constructor(private router: Router, private authService: AuthService) {
  }

  ngOnInit(): void {
    // check if user is logged and update flag
    this.logged = this.authService.isLogged();
  }

  authButtonClicked(): void {
    if(this.logged) {
      // click on logout; logout and redirect to homepage
      this.logged = false;
      // delete access token from app state
      this.authService.logout();
      this.router.navigate(['/']);
    } else {
      // click on login; redirect to related page
      this.router.navigate(['/signin']);
    }
  }
}

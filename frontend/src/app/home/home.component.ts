import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  constructor(private router: Router, private authService: AuthService) {
  }

  ngOnInit(): void {
    // if user is logged, redirect to role-related homepage
    if(this.authService.isLogged()) {
      const role = this.authService.getUserData().roles[0].split('_')[1].toLowerCase();
      this.router.navigate([`/${role}`]);
    }
  }

  getStartedButtonClicked() {
    // head to login page
    this.router.navigate(['/signin']);
  }

}

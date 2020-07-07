import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { Router } from '@angular/router';

import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-container',
  templateUrl: './container.component.html',
  styleUrls: ['./container.component.css']
})
export class ContainerComponent implements OnInit {
  logged: boolean = false;
  
  @ViewChild(MatSidenav)
  matSideNav: MatSidenav;

  constructor(private router: Router, private authService: AuthService) {
  }

  ngOnInit(): void {
    this.logged = this.authService.isLogged();
  }

  menuIconClicked(): void {
    this.matSideNav.toggle();
  }
  authButtonClicked(): void {
    if(this.logged) {
      this.logged = false;
      this.authService.logout();
      this.router.navigate(['/']);
    } else
      this.router.navigate(['/signin']);
  }
}

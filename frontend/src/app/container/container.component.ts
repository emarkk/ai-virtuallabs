import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../core/services/auth.service';
import { SideNavService } from '../core/services/sidenav.service';

@Component({
  selector: 'app-container',
  templateUrl: './container.component.html',
  styleUrls: ['./container.component.css']
})
export class ContainerComponent implements OnInit {
  logged: boolean = false;

  constructor(private router: Router, private authService: AuthService, private sidenavService: SideNavService) {
  }

  ngOnInit(): void {
    this.logged = this.authService.isLogged();
  }

  menuIconClicked(): void {
    this.sidenavService.toggle();
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

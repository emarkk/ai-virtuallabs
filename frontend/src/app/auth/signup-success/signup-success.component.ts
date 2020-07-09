import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from 'src/app/core/services/auth.service';
import { RegistrationService } from 'src/app/core/services/registration.service';

@Component({
  selector: 'app-signup-success',
  templateUrl: './signup-success.component.html',
  styleUrls: ['./signup-success.component.css']
})
export class SignUpSuccessComponent implements OnInit {

  constructor(private router: Router, private authService: AuthService, private registrationService: RegistrationService) {
  }

  ngOnInit(): void {
    if(this.authService.isLogged() || !this.registrationService.hasRegisteredSuccessfully())
      this.router.navigate(['/']);
  }

}

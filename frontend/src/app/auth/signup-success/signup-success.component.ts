import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { RegistrationService } from 'src/app/core/services/registration.service';

@Component({
  selector: 'app-signup-success',
  templateUrl: './signup-success.component.html',
  styleUrls: ['./signup-success.component.css']
})
export class SignUpSuccessComponent implements OnInit {

  constructor(private router: Router, private registrationService: RegistrationService) {
  }

  ngOnInit(): void {
    if(!this.registrationService.hasRegisteredSuccessfully())
      this.router.navigate(['/']);
  }

}

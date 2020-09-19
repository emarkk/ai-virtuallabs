import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { APIResult } from 'src/app/core/models/api-result.model';

import { AuthService } from 'src/app/core/services/auth.service';
import { RegistrationService } from 'src/app/core/services/registration.service';

@Component({
  selector: 'app-confirm',
  templateUrl: './confirm.component.html',
  styleUrls: ['./confirm.component.css']
})
export class ConfirmComponent implements OnInit {
  // result of account confirmation
  result$: Observable<APIResult>;

  constructor(private router: Router, private route: ActivatedRoute, private authService: AuthService, private registrationService: RegistrationService) {
  }

  ngOnInit(): void {
    // if already logged in, should not be here
    if(this.authService.isLogged()) {
      this.router.navigate(['/']);
      return;
    }

    // confirm account
    this.result$ = this.registrationService.confirm(this.route.snapshot.params.token);
  }

}

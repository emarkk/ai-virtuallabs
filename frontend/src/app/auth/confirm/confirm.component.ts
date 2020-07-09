import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { AuthService } from 'src/app/core/services/auth.service';
import { RegistrationService } from 'src/app/core/services/registration.service';

@Component({
  selector: 'app-confirm',
  templateUrl: './confirm.component.html',
  styleUrls: ['./confirm.component.css']
})
export class ConfirmComponent implements OnInit {
  result$: Observable<Boolean>;

  constructor(private router: Router, private route: ActivatedRoute, private authService: AuthService, private registrationService: RegistrationService) {
  }

  ngOnInit(): void {
    if(this.authService.isLogged()) {
      this.router.navigate(['/']);
      return;
    }

    this.route.params.subscribe(params => {
      const token = params.token;
      this.result$ = this.registrationService.confirm(token);
    });
  }

}

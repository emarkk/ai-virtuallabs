import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';

import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {

  constructor(private router: Router, private authService: AuthService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const currentUser = this.authService.getUserData();
    if(currentUser) {
      if(route.data.roles && !route.data.roles.some(r => currentUser.roles.includes(r))) {
        this.router.navigate(['/']);
        return false;
      }
      return true;
    }
    this.router.navigate(['/signin'], { queryParams: { redirect: state.url }});
    return false;
  }

}
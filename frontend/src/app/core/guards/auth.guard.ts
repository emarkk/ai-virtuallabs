import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';

import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private router: Router, private authService: AuthService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    // get user data
    const currentUser = this.authService.getUserData();

    if(currentUser) {
      // user is authenticated, check authorization
      if(route.data.roles && !route.data.roles.some(r => currentUser.roles.includes(r))) {
        // insufficient authorization, redirect to home
        this.router.navigate(['/']);
        return false;
      }
      // ok, proceed
      return true;
    }

    // user not authenticated, redirect to login (and include desired return path)
    this.router.navigate(['/signin'], { queryParams: { redirect: state.url }});
    return false;
  }

}
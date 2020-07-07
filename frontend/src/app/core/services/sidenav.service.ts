import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SideNavService {
  private sidenav = new Subject();

  get() {
    return this.sidenav;
  }

  toggle() {
    this.sidenav.next();
  }

}
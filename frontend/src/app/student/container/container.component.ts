import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';

import { SideNavService } from 'src/app/core/services/sidenav.service';

@Component({
  selector: 'app-student-container',
  templateUrl: './container.component.html',
  styleUrls: ['./container.component.css']
})
export class StudentContainerComponent implements OnInit {

  @ViewChild(MatSidenav)
  private matSideNav: MatSidenav;

  constructor(private sidenavService: SideNavService) {
  }

  ngOnInit(): void {
    this.sidenavService.get().subscribe(() => {
      this.matSideNav.toggle();
    });
  }

}

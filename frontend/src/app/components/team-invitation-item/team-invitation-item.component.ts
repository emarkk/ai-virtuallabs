import { Component, OnInit, Input } from '@angular/core';

import { Team } from 'src/app/core/models/team.model';

@Component({
  selector: 'app-team-invitation-item',
  templateUrl: './team-invitation-item.component.html',
  styleUrls: ['./team-invitation-item.component.css']
})
export class TeamInvitationItemComponent implements OnInit {
  invitation: Team

  @Input() set data(value: Team) {
    this.invitation = value;
  }

  constructor() {
  }

  ngOnInit(): void {
  }

}
import { Component, OnInit, Input } from '@angular/core';

import { Team } from 'src/app/core/models/team.model';

@Component({
  selector: 'app-team-invitation-list',
  templateUrl: './team-invitation-list.component.html',
  styleUrls: ['./team-invitation-list.component.css']
})
export class TeamInvitationListComponent implements OnInit {
  invitationList: Team[]

  @Input() set invitations(data: Team[]) {
    this.invitationList = data;
  }

  constructor() {
  }

  ngOnInit(): void {
  }

}
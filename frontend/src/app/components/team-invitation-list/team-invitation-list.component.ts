import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

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

  @Output() invitationAccept = new EventEmitter<number>();
  @Output() invitationDecline = new EventEmitter<number>();

  constructor() {
  }

  ngOnInit(): void {
  }

  invitationAccepted(teamId: number) {
    this.invitationAccept.emit(teamId);
  }
  invitationDeclined(teamId: number) {
    this.invitationDecline.emit(teamId);
  }
}
import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

import { Team, TeamStatus } from 'src/app/core/models/team.model';
import { timeString } from 'src/app/core/utils';

@Component({
  selector: 'app-team-invitation-item',
  templateUrl: './team-invitation-item.component.html',
  styleUrls: ['./team-invitation-item.component.css']
})
export class TeamInvitationItemComponent implements OnInit {
  invitation: Team;
  enabled: boolean;
  expiration: string;
  expirationUpdateHandle: number;

  @Input() set data(value: Team) {
    this.invitation = value;
    this.enabled = this.invitation.status == TeamStatus.PROVISIONAL;
    this.expiration = '...';

    clearInterval(this.expirationUpdateHandle);
    this.expirationUpdateHandle = window.setInterval(() => {
      this.expiration = timeString(this.invitation.invitationsExpiration.getTime() - new Date().getTime());
    }, 1000);
  }

  @Output() accept = new EventEmitter<void>();
  @Output() decline = new EventEmitter<void>();

  constructor() {
  }

  ngOnInit(): void {
  }

  acceptClicked() {
    this.accept.emit();
  }
  declineClicked() {
    this.decline.emit();
  }
}
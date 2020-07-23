import { Pipe, PipeTransform } from '@angular/core';

import { TeamMember, TeamInvitationStatus } from '../models/team.model';

@Pipe({ name: 'teamCreator' })
export class TeamCreatorPipe implements PipeTransform {
  transform(members: TeamMember[]): TeamMember[] {
    return members.filter(m => m.status == TeamInvitationStatus.CREATOR);
  }
}

@Pipe({ name: 'teamGuests' })
export class TeamGuestsPipe implements PipeTransform {
  transform(members: TeamMember[]): TeamMember[] {
    return members.filter(m => m.status != TeamInvitationStatus.CREATOR);
  }
}
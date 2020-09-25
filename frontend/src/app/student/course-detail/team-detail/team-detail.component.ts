import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable, BehaviorSubject } from 'rxjs';
import { switchMap } from 'rxjs/operators';

import { APIResult } from 'src/app/core/models/api-result.model';
import { Team, TeamStatus, TeamInvitationStatus } from 'src/app/core/models/team.model';

import { StudentService } from 'src/app/core/services/student.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { TeamService } from 'src/app/core/services/team.service';
import { ToastService } from 'src/app/core/services/toast.service';

import { ConfirmDialog } from 'src/app/components/dialogs/confirm/confirm.component';

import { timeString } from 'src/app/core/utils';

@Component({
  selector: 'app-student-course-team-detail',
  templateUrl: './team-detail.component.html',
  styleUrls: ['./team-detail.component.css']
})
export class StudentCourseTeamDetailComponent implements OnInit {
  InvitationStatus = TeamInvitationStatus;

  courseCode: string;
  
  @Input() set course(value: string) {
    this.courseCode = value;
  }
  
  teams$: Observable<Team[]>;
  teamsRefreshToken = new BehaviorSubject(undefined);

  team: Team;
  acceptedTeam: Team;
  proposalExpiration: string = '...';

  activeTeamInvitations: Team[] = [];
  inactiveTeamInvitations: Team[] = [];

  @Output() completeTeam = new EventEmitter<Team>();

  constructor(private authService: AuthService, private studentService: StudentService, private teamService: TeamService, private dialog: MatDialog, private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.teams$ = this.teamsRefreshToken.pipe(
      switchMap(() => this.studentService.getTeamsForCourse(this.authService.getId(), this.courseCode))
    );

    this.teams$.subscribe(teams => {
      this.team = teams.find(t => t.status == TeamStatus.COMPLETE);

      if(this.team) {
        this.completeTeam.emit(this.team);
      } else {
        this.completeTeam.emit(null);
        this.acceptedTeam = teams.find(t => t.status == TeamStatus.PROVISIONAL && this.isAccepted(t));
        if(this.acceptedTeam) {
          setInterval(() => {
            this.proposalExpiration = this.timeTo(this.acceptedTeam.invitationsExpiration);
          }, 1000);
        }
      }

      this.activeTeamInvitations = teams.filter(t => t.status == TeamStatus.PROVISIONAL && !this.isAccepted(t));
      this.inactiveTeamInvitations = teams.filter(t => [TeamStatus.ABORTED, TeamStatus.EXPIRED].includes(t.status));
    });
  }

  timeTo(date) {
    return timeString(date.getTime() - new Date().getTime());
  }
  isAccepted(team: Team) {
    return [TeamInvitationStatus.ACCEPTED, TeamInvitationStatus.CREATOR].includes(team.getMemberStatus(this.authService.getId()));
  }
  teamInvitationAccepted(teamId: number) {
    this.dialog.open(ConfirmDialog, {
      data: {
        title: 'Accept invitation',
        message: 'Do you confirm you want to accept this invitation?'
      }
    }).afterClosed().subscribe(confirmed => {
      if(confirmed) {
        this.teamService.acceptTeamInvitation(teamId).subscribe((res: APIResult) => {
          if(res.ok) {
            this.teamsRefreshToken.next(undefined);
            this.toastService.show({ type: 'success', text: 'Team invitation accepted successfully.' });
          } else
            this.toastService.show({ type: 'danger', text: res.errorMessage });
        });
      }
    });
  }
  teamInvitationDeclined(teamId: number) {
    this.dialog.open(ConfirmDialog, {
      data: {
        title: 'Decline invitation',
        message: 'Are you sure you want to decline this team invitation?'
      }
    }).afterClosed().subscribe(confirmed => {
      if(confirmed) {
        this.teamService.declineTeamInvitation(teamId).subscribe((res: APIResult) => {
          if(res.ok) {
            this.teamsRefreshToken.next(undefined);
            this.toastService.show({ type: 'success', text: 'Team invitation declined successfully.' });
          } else
            this.toastService.show({ type: 'danger', text: res.errorMessage });
        });
      }
    });
  }
}

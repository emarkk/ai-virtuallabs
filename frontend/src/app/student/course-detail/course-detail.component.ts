import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';

import { Course } from 'src/app/core/models/course.model';
import { Professor } from 'src/app/core/models/professor.model';
import { Team, TeamStatus, TeamInvitationStatus } from 'src/app/core/models/team.model';

import { CourseService } from 'src/app/core/services/course.service';
import { StudentService } from 'src/app/core/services/student.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { TeamService } from 'src/app/core/services/team.service';
import { ToastService } from 'src/app/core/services/toast.service';

import { ConfirmDialog } from 'src/app/components/dialogs/confirm/confirm.component';

import { navHome, navCourses, nav } from '../student.navdata';

import { timeString } from 'src/app/core/utils';

@Component({
  selector: 'app-student-course-detail',
  templateUrl: './course-detail.component.html',
  styleUrls: ['./course-detail.component.css']
})
export class StudentCourseDetailComponent implements OnInit {
  courseCode: string;
  courseName: string;
  courseEnabled: boolean;
  course$: Observable<Course>;
  navigationData: Array<any>|null = null;

  professors$: Observable<Professor[]>;
  teams$: Observable<Team[]>;

  team: Team;
  acceptedTeam: Team;
  proposalExpiration: string = '...';

  activeTeamInvitations: Team[] = [];
  inactiveTeamInvitations: Team[] = [];

  constructor(private route: ActivatedRoute, private authService: AuthService, private courseService: CourseService, private studentService: StudentService, private teamService: TeamService, private dialog: MatDialog, private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseCode = params.code;
      this.course$ = this.courseService.get(this.courseCode);
      this.professors$ = this.courseService.getProfessors(this.courseCode);
      this.teams$ = this.studentService.getTeamsForCourse(this.authService.getId(), this.courseCode);

      this.course$.subscribe(course => {
        this.courseName = course.name;
        this.courseEnabled = course.enabled;
        this.navigationData = [navHome, navCourses, nav(course.name, `/professor/course/${course.code}`)];
      });
      this.teams$.subscribe(teams => {
        if(!teams.length)
          return;
        
        this.team = teams.find(t => t.status == TeamStatus.COMPLETE);

        if(!this.team) {
          this.acceptedTeam = teams.find(t => t.status == TeamStatus.PROVISIONAL && this.isAccepted(t));
          if(this.acceptedTeam) {
            setInterval(() => {
              this.proposalExpiration = this.timeTo(this.acceptedTeam.invitationsExpiration);
            }, 1000);
          }
        }

        this.activeTeamInvitations = teams.filter(t => t.status == TeamStatus.PROVISIONAL && !this.isAccepted(t));
        this.inactiveTeamInvitations = teams.filter(t => [TeamStatus.ABORTED, TeamStatus.EXPIRED].includes(t.status) && !this.isAccepted(t));
      });
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
        message: `Do you confirm you want to accept this invitation?`
      }
    }).afterClosed().subscribe(confirmed => {
      if(confirmed) {
        this.teamService.acceptTeamInvitation(teamId).subscribe(res => {
          if(res) {
            this.toastService.show({ type: 'success', text: 'Team invitation accepted successfully.' });
          } else
            this.toastService.show({ type: 'danger', text: 'An error occurred.' });
        });
      }
    });
  }
  teamInvitationDeclined(teamId: number) {
    this.dialog.open(ConfirmDialog, {
      data: {
        title: 'Decline invitation',
        message: `Are you sure you want to decline this team invitation?`
      }
    }).afterClosed().subscribe(confirmed => {
      if(confirmed) {
        this.teamService.declineTeamInvitation(teamId).subscribe(res => {
          if(res) {
            this.toastService.show({ type: 'success', text: 'Team invitation declined successfully.' });
          } else
            this.toastService.show({ type: 'danger', text: 'An error occurred.' });
        });
      }
    });
  }
}

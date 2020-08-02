import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';
import { Observable, combineLatest } from 'rxjs';

import { Course } from 'src/app/core/models/course.model';
import { Vm } from 'src/app/core/models/vm.model';
import { Team, TeamStatus } from 'src/app/core/models/team.model';

import { CourseService } from 'src/app/core/services/course.service';

import { VmLimitsDialog } from 'src/app/components/dialogs/vm-limits/vm-limits.component';

import { navHome, navCourses, nav } from '../professor.navdata';

@Component({
  selector: 'app-professor-vms',
  templateUrl: './vms.component.html',
  styleUrls: ['./vms.component.css']
})
export class ProfessorVmsComponent implements OnInit {
  courseCode: string;
  course$: Observable<Course>;
  
  teamsVms$: Observable<{ team: Team, vm: Vm }[]>;
  columnsToDisplay: string[] = ['team', 'vm', 'creator', 'online', '_connect'];
  
  vmLimitsDialogRef: MatDialogRef<VmLimitsDialog> = null;

  navigationData: Array<any>|null = null;

  constructor(private route: ActivatedRoute, private router: Router, private courseService: CourseService, private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.courseCode = this.route.snapshot.params.code;
    this.init();
  }
  init() {
    this.course$ = this.courseService.get(this.courseCode);
    this.teamsVms$ = this.courseService.getTeamsAndVms(this.courseCode);

    this.course$.subscribe(course => {
      this.navigationData = [navHome, navCourses, nav(course.name, `/professor/course/${course.code}`), nav('VMs')];
    });

    combineLatest(this.route.queryParams, this.teamsVms$).subscribe(([queryParams, teamsVms]) => {
      console.log(teamsVms);
      if(teamsVms && queryParams.edit == 'vm-limits') {
        const team = teamsVms.find(tv => tv.team.id == queryParams.team).team;
        if(!team)
          this.router.navigate([]);

        this.vmLimitsDialogRef = this.dialog.open(VmLimitsDialog, {
          data: {
            teamName: team.name
          }
        });
        this.vmLimitsDialogRef.afterClosed().subscribe(res => {
          this.router.navigate([]);
        });
      } else if(this.vmLimitsDialogRef)
        this.vmLimitsDialogRef.close();
    });
  }
  
  getRowSpan(i: number, teamsVms: { team: Team, vm: Vm }[]): number {
    const currentTeamId = teamsVms[i].team.id;
    return teamsVms.filter(x => x.team.id == currentTeamId).length;
  }
  display(i: number, teamsVms: { team: Team, vm: Vm }[]): boolean {
    return i == 0 || teamsVms[i].team.id != teamsVms[i-1].team.id;
  }
}
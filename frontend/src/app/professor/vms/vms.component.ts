import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { Course } from 'src/app/core/models/course.model';
import { Vm } from 'src/app/core/models/vm.model';
import { Team, TeamStatus } from 'src/app/core/models/team.model';

import { CourseService } from 'src/app/core/services/course.service';

import { navHome, navCourses, nav } from '../professor.navdata';

@Component({
  selector: 'app-professor-vms',
  templateUrl: './vms.component.html',
  styleUrls: ['./vms.component.css']
})
export class ProfessorVmsComponent implements OnInit {
  courseCode: string;
  course$: Observable<Course>;
  
  teamsVms: { team: Team, vm: Vm }[];
  columnsToDisplay: string[] = ['team', 'vm', 'creator', 'online', '_connect'];

  navigationData: Array<any>|null = null;

  constructor(private route: ActivatedRoute, private courseService: CourseService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseCode = params.code;
      this.course$ = this.courseService.get(this.courseCode);

      this.courseService.getTeamsAndVms(this.courseCode).subscribe(teamsVms => this.teamsVms = teamsVms);

      this.course$.subscribe(course => {
        this.navigationData = [navHome, navCourses, nav(course.name, `/professor/course/${course.code}`), nav('VMs')];
      });
    });
  }
  
  getRowSpan(i: number): number {
    const currentTeamId = this.teamsVms[i].team.id;
    return this.teamsVms.filter(tv => tv.team.id == currentTeamId).length;
  }
  display(i: number): boolean {
    return i == 0 || this.teamsVms[i].team.id != this.teamsVms[i-1].team.id;
  }
}

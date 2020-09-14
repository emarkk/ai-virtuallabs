import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Course } from 'src/app/core/models/course.model';
import { Team, TeamStatus } from 'src/app/core/models/team.model';

import { CourseService } from 'src/app/core/services/course.service';

import { navHome, navCourses, nav } from '../professor.navdata';

const filters = {
  active: (team: Team) => [TeamStatus.COMPLETE, TeamStatus.PROVISIONAL].includes(team.status),
  complete: (team: Team) => team.status == TeamStatus.COMPLETE,
  inactive: (team: Team) => [TeamStatus.ABORTED, TeamStatus.EXPIRED].includes(team.status)
};

@Component({
  selector: 'app-professor-teams',
  templateUrl: './teams.component.html',
  styleUrls: ['./teams.component.css']
})
export class ProfessorTeamsComponent implements OnInit {
  courseCode: string;
  course$: Observable<Course>;

  teams$: Observable<Team[]>;
  teamFilter: string = null;
  filterFunction = filters.active;
  columnsToDisplay: string[] = ['id', 'name', 'status'];

  navigationData$: Observable<Array<any>>;

  constructor(private route: ActivatedRoute, private courseService: CourseService) {
  }

  ngOnInit(): void {
    this.courseCode = this.route.snapshot.params.code;
    this.init();
  }
  init(): void {
    this.course$ = this.courseService.get(this.courseCode);
    this.teams$ = this.courseService.getTeams(this.courseCode);
    this.navigationData$ = this.course$.pipe(
      map(course => [navHome, navCourses, nav(course.name, `/professor/course/${course.code}`), nav('Teams')])
    );

    this.route.queryParams.subscribe(queryParams => {
      if(queryParams.filter && ['complete', 'inactive'].includes(queryParams.filter)) {
        this.teamFilter = queryParams.filter;
        this.filterFunction = filters[this.teamFilter];
      } else {
        this.teamFilter = null;
        this.filterFunction = filters.active;
      }
    });
  }
  
}

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { Course } from 'src/app/core/models/course.model';
import { Team } from 'src/app/core/models/team.model';

import { CourseService } from 'src/app/core/services/course.service';

import { navHome, navCourses, nav } from '../professor.navdata';

@Component({
  selector: 'app-professor-course-teams',
  templateUrl: './course-teams.component.html',
  styleUrls: ['./course-teams.component.css']
})
export class ProfessorCourseTeamsComponent implements OnInit {
  courseCode: string;
  course$: Observable<Course>;
  teams$: Observable<Team[]>;
  navigationData: Array<any>|null = null;

  constructor(private route: ActivatedRoute, private courseService: CourseService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseCode = params.code;
      this.course$ = this.courseService.get(this.courseCode);
      this.teams$ = this.courseService.getTeams(this.courseCode);

      this.course$.subscribe(course => {
        this.navigationData = [navHome, navCourses, nav(course.name, '/professor/course/' + course.code), nav('Teams')];
      });
    });
  }
  
}

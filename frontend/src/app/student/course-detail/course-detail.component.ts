import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Course } from 'src/app/core/models/course.model';
import { Professor } from 'src/app/core/models/professor.model';
import { Team } from 'src/app/core/models/team.model';

import { CourseService } from 'src/app/core/services/course.service';

import { navHome, navCourses, nav } from '../student.navdata';

@Component({
  selector: 'app-student-course-detail',
  templateUrl: './course-detail.component.html',
  styleUrls: ['./course-detail.component.css']
})
export class StudentCourseDetailComponent implements OnInit {
  courseCode: string;
  course$: Observable<Course>;
  professors$: Observable<Professor[]>;

  navigationData$: Observable<Array<any>>;

  team: Team;
  vmAction: { edit: string, vm: number };

  constructor(private route: ActivatedRoute, private courseService: CourseService) {
  }

  ngOnInit(): void {
    this.courseCode = this.route.snapshot.params.code;
    this.init();
  }
  init(): void {
    this.course$ = this.courseService.get(this.courseCode);
    this.professors$ = this.courseService.getProfessors(this.courseCode);
    this.navigationData$ = this.course$.pipe(
      map(course => [navHome, navCourses, nav(course.name)])
    );
  }

  joinedTeam(team: Team) {
    this.team = team;
  }
}

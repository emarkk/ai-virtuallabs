import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { forkJoin, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Course } from 'src/app/core/models/course.model';
import { Homework } from 'src/app/core/models/homework.model';

import { CourseService } from 'src/app/core/services/course.service';
import { HomeworkService } from 'src/app/core/services/homework.service';

import { navHome, navCourses, nav } from '../professor.navdata';

@Component({
  selector: 'app-professor-homework-detail',
  templateUrl: './homework-detail.component.html',
  styleUrls: ['./homework-detail.component.css']
})
export class ProfessorHomeworkDetailComponent implements OnInit {
  courseCode: string;
  homeworkId: number;
  course$: Observable<Course>;
  homework$: Observable<Homework>;
  homeworkText$: Observable<string>;
  
  navigationData$: Observable<Array<any>>;

  constructor(private route: ActivatedRoute, private courseService: CourseService, private homeworkService: HomeworkService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseCode = params.code;
      this.homeworkId = params.id;

      this.course$ = this.courseService.get(this.courseCode);
      this.homework$ = this.homeworkService.get(this.homeworkId);
      this.homeworkText$ = this.homeworkService.getText(this.homeworkId);
      this.navigationData$ = forkJoin([this.course$, this.homework$]).pipe(
        map(([course, homework]) => [navHome, navCourses, nav(course.name, `/professor/course/${course.code}`), nav('Homeworks', `/professor/course/${course.code}/homeworks`), nav(homework.title)])
      );
    });
  }

}
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { forkJoin, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Course } from 'src/app/core/models/course.model';
import { Homework } from 'src/app/core/models/homework.model';

import { CourseService } from 'src/app/core/services/course.service';
import { HomeworkService } from 'src/app/core/services/homework.service';

import { navHome, navCourses, nav } from '../student.navdata';

@Component({
  selector: 'app-student-homework-detail',
  templateUrl: './homework-detail.component.html',
  styleUrls: ['./homework-detail.component.css']
})
export class StudentHomeworkDetailComponent implements OnInit, OnDestroy {
  courseCode: string;
  homeworkId: number;

  course$: Observable<Course>;
  homework$: Observable<Homework>;
  homeworkText: SafeUrl;

  navigationData$: Observable<Array<any>>;

  constructor(private route: ActivatedRoute, private courseService: CourseService, private homeworkService: HomeworkService, private domSanitizer: DomSanitizer) {
  }

  ngOnInit(): void {
    this.courseCode = this.route.snapshot.params.code;
    this.homeworkId = this.route.snapshot.params.id;
    this.init();
  }
  init(): void {
    this.course$ = this.courseService.get(this.courseCode);
    this.homework$ = this.homeworkService.get(this.homeworkId);
    this.navigationData$ = forkJoin([this.course$, this.homework$]).pipe(
      map(([course, homework]) => [navHome, navCourses, nav(course.name, `/student/course/${course.code}`), nav('Homeworks', `/student/course/${course.code}/homeworks`), nav(homework.title)])
    );
    
    this.homeworkService.getText(this.homeworkId).subscribe(homeworkText => this.homeworkText = homeworkText);
  }

  ngOnDestroy() {
    URL.revokeObjectURL(this.homeworkText as string);
  }

}

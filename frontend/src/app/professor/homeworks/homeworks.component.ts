import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Course } from 'src/app/core/models/course.model';
import { Homework } from 'src/app/core/models/homework.model';

import { CourseService } from 'src/app/core/services/course.service';

import { navHome, navCourses, nav } from '../professor.navdata';

@Component({
  selector: 'app-professor-homeworks',
  templateUrl: './homeworks.component.html',
  styleUrls: ['./homeworks.component.css']
})
export class ProfessorHomeworksComponent implements OnInit {
  courseCode: string;
  course$: Observable<Course>;
  homeworks$: Observable<Homework[]>;

  navigationData$: Observable<Array<any>>;

  constructor(private route: ActivatedRoute, private courseService: CourseService) {
  }

  ngOnInit(): void {
    this.courseCode = this.route.snapshot.params.code;
    this.init();
  }
  init(): void {
    this.course$ = this.courseService.get(this.courseCode);
    this.homeworks$ = this.courseService.getHomeworks(this.courseCode).pipe(
      map(homeworks => homeworks.map(h => { h.link = `/professor/course/${this.courseCode}/homework/${h.id}`; return h; }))
    );
    this.navigationData$ = this.course$.pipe(
      map(course => [navHome, navCourses, nav(course.name, `/professor/course/${course.code}`), nav('Homeworks')])
    );
  }

}

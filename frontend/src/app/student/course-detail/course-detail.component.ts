import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { Course } from 'src/app/core/models/course.model';
import { Professor } from 'src/app/core/models/professor.model';
import { CourseService } from 'src/app/core/services/course.service';

import { navHome, navCourses, nav } from '../student.navdata';

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

  constructor(private route: ActivatedRoute, private courseService: CourseService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseCode = params.code;
      this.course$ = this.courseService.get(this.courseCode);
      this.professors$ = this.courseService.getProfessors(this.courseCode);

      this.course$.subscribe(course => {
        this.courseName = course.name;
        this.courseEnabled = course.enabled;
        this.navigationData = [navHome, navCourses, nav(course.name, `/professor/course/${course.code}`)];
      });
    });
  }

}

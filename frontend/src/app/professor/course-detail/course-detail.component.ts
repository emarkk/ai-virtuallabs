import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { Course } from 'src/app/core/models/course.model';
import { Professor } from 'src/app/core/models/professor.model';
import { CourseService } from 'src/app/core/services/course.service';

import { navHome, navCourses, nav } from '../professor.navdata';

@Component({
  selector: 'app-professor-course-detail',
  templateUrl: './course-detail.component.html',
  styleUrls: ['./course-detail.component.css']
})
export class ProfessorCourseDetailComponent implements OnInit {
  courseCode: string;
  course$: Observable<Course>;
  professors$: Observable<Professor[]>;
  navigationData: Array<any>|null = null;
  updatingStatus: boolean = false;

  constructor(private route: ActivatedRoute, private courseService: CourseService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseCode = params.code;
      this.course$ = this.courseService.get(this.courseCode);
      this.professors$ = this.courseService.getProfessors(this.courseCode);

      this.course$.subscribe(course => {
        this.navigationData = [navHome, navCourses, nav(course.name, '/professor/course/' + course.code)];
      });      
    });
  }

  statusButtonClicked() {
    this.updatingStatus = true;
    /*if(this.course£.) {
      this.courseService.disable(this.courseCode).subscribe(res => {
        this.updatingStatus = false;
        if(res)
          this.course.enabled = false;
      });
    } else {
      this.courseService.enable(this.courseCode).subscribe(res => {
        this.updatingStatus = false;
        if(res)
          this.course.enabled = true;
      });
    }*/
  }
}

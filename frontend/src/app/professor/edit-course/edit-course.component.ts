import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';

import { CourseService } from 'src/app/core/services/course.service';
import { CourseFormComponent } from 'src/app/components/course-form/course-form.component';

import { navHome, navCourses, nav } from '../professor.navdata';

@Component({
  selector: 'app-professor-edit-course',
  templateUrl: './edit-course.component.html',
  styleUrls: ['./edit-course.component.css']
})
export class ProfessorEditCourseComponent implements OnInit {
  navigationData: Array<any> = [
    navHome,
    navCourses,
    nav('Edit course')
  ];
  
  @ViewChild(CourseFormComponent)
  formComponent: CourseFormComponent;

  constructor(private router: Router, private courseService: CourseService) {
  }

  ngOnInit(): void {

  }

  saveCourse(courseData) {
  }

}

import { Component, OnInit } from '@angular/core';

import { Course } from 'src/app/core/models/course.model';
import { navHome, navCourses } from '../professor.navdata';

@Component({
  selector: 'app-professor-courses',
  templateUrl: './courses.component.html',
  styleUrls: ['./courses.component.css']
})
export class ProfessorCoursesComponent implements OnInit {
  navigationData: Array<any> = [
    navHome,
    navCourses
  ];
  courses: Course[] = [];

  constructor() {
  }

  ngOnInit(): void {

  }

}

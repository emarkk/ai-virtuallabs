import { Component, OnInit } from '@angular/core';

import { navHome, navCourses, navNewCourse } from '../professor.navdata';

@Component({
  selector: 'app-professor-new-course',
  templateUrl: './new-course.component.html',
  styleUrls: ['./new-course.component.css']
})
export class ProfessorNewCourseComponent implements OnInit {
  navigationData: Array<any> = [
    navHome,
    navCourses,
    navNewCourse
  ];

  constructor() {
  }

  ngOnInit(): void {

  }

}

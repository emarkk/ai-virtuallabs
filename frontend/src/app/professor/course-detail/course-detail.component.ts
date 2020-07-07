import { Component, OnInit } from '@angular/core';

import { navHome, navCourses } from '../professor.navdata';

@Component({
  selector: 'app-professor-course-detail',
  templateUrl: './course-detail.component.html',
  styleUrls: ['./course-detail.component.css']
})
export class ProfessorCourseDetailComponent implements OnInit {
  navigationData: Array<any> = [
    navHome,
    navCourses
  ];

  constructor() {
  }

  ngOnInit(): void {
  }

}

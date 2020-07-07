import { Component, OnInit } from '@angular/core';

import { Course } from 'src/app/core/models/course.model';

@Component({
  selector: 'app-professor-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class ProfessorHomeComponent implements OnInit {
  courses: Course[] = [];

  constructor() {
  }

  ngOnInit(): void {
  }

}

import { Component, OnInit, Input } from '@angular/core';

import { Course } from 'src/app/core/models/course.model';

@Component({
  selector: 'app-course-list',
  templateUrl: './course-list.component.html',
  styleUrls: ['./course-list.component.css']
})
export class CourseListComponent implements OnInit {
  courseList: Course[];

  @Input() set courses(data: Course[]) {
    this.courseList = data;
  }

  constructor() {
  }

  ngOnInit(): void {
  }

}
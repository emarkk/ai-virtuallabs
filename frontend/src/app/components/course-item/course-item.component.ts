import { Component, OnInit, Input } from '@angular/core';

import { Course } from 'src/app/core/models/course.model';

@Component({
  selector: 'app-course-item',
  templateUrl: './course-item.component.html',
  styleUrls: ['./course-item.component.css']
})
export class CourseItemComponent implements OnInit {
  course: Course;

  @Input() set data(value: Course) {
    this.course = value;
  }

  constructor() {
  }

  ngOnInit(): void {
  }

}
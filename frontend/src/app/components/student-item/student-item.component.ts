import { Component, OnInit, Input } from '@angular/core';

import { Student } from 'src/app/core/models/student.model';

@Component({
  selector: 'app-student-item',
  templateUrl: './student-item.component.html',
  styleUrls: ['./student-item.component.css']
})
export class StudentItemComponent implements OnInit {
  student: Student;

  @Input() set data(value: Student) {
    this.student = value;
  }

  constructor() {
  }

  ngOnInit(): void {
  }

}
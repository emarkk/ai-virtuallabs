import { Component, OnInit, Input } from '@angular/core';

import { Student } from 'src/app/core/models/student.model';

@Component({
  selector: 'app-student-list',
  templateUrl: './student-list.component.html',
  styleUrls: ['./student-list.component.css']
})
export class StudentListComponent implements OnInit {
  studentList: Student[] = null;

  @Input() set students(data: Student[]) {
    this.studentList = data;
  }

  constructor() {
  }

  ngOnInit(): void {
  }

}
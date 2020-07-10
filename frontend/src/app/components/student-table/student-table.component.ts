import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { MatCheckboxChange } from '@angular/material/checkbox';

@Component({
  selector: 'app-student-table',
  templateUrl: './student-table.component.html',
  styleUrls: ['./student-table.component.css']
})
export class StudentTableComponent implements OnInit {
  studentList: any = null;

  checkedSet = new Set<number>();
  studentsMasterChecked: boolean = false;
  studentsMasterSemichecked: boolean = false;

  columnsToDisplay = ['_select', 'id', 'picture', 'firstName', 'lastName', 'teamName'];

  @Input() set students(value: any) {
    this.studentList = value;
  }

  @Output() select = new EventEmitter<Set<number>>();

  constructor() {
  }

  ngOnInit(): void {
  }

  setMasterState(event: MatCheckboxChange) {

  }
  getCheckedState(studentId: number) {
    return this.checkedSet.has(studentId);
  }
  setCheckedState(studentId: number, event: MatCheckboxChange) {
    event.checked ? this.checkedSet.add(studentId) : this.checkedSet.delete(studentId);
    this.select.emit(this.checkedSet);
  }
}
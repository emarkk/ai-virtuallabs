import { Component, OnInit, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Observable, merge, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

import { Student } from 'src/app/core/models/student.model';

import { StudentsDataSource } from 'src/app/core/datasources/students.datasource';

@Component({
  selector: 'app-student-table',
  templateUrl: './student-table.component.html',
  styleUrls: ['./student-table.component.css']
})
export class StudentTableComponent implements OnInit {
  studentsDataSource: StudentsDataSource = null;

  checkedSet = new Set<number>();
  studentsMasterChecked: boolean = false;
  studentsMasterSemichecked: boolean = false;
  allDatasetSelected: boolean = false;

  columnsToDisplay = ['_select', 'id', 'picture', 'firstName', 'lastName', 'teamName'];

  @ViewChild(MatPaginator)
  paginator: MatPaginator;

  @ViewChild(MatSort)
  sort: MatSort;

  @Input() set dataSource(value: StudentsDataSource) {
    this.studentsDataSource = value;
  }

  @Output() select = new EventEmitter<Set<number>>();

  constructor() {
  }

  ngOnInit(): void {
    this.studentsDataSource.loadStudents();
  }

  ngAfterViewInit() {
    this.sort.sortChange.subscribe(() => {
      this.checkedSet = new Set();
      this.paginator.pageIndex = 0;
    });

    merge(this.sort.sortChange, this.paginator.page).pipe(
      tap(() => this.loadStudentsPage())
    ).subscribe();
  }

  loadStudentsPage() {
    this.studentsDataSource.loadStudents(this.sort.active, this.sort.direction, this.paginator.pageIndex, this.paginator.pageSize);
  }
  setMasterState() {
  }
  getCheckedState(studentId: number) {
    return this.checkedSet.has(studentId);
  }
  setCheckedState(studentId: number, event: MatCheckboxChange) {
    event.checked ? this.checkedSet.add(studentId) : this.checkedSet.delete(studentId);
    this.select.emit(this.checkedSet);
  }
}
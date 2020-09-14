import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { Observable, Subscription, Subject } from 'rxjs';
import { debounceTime, map } from 'rxjs/operators';

import { Course } from 'src/app/core/models/course.model';

import { CourseService } from 'src/app/core/services/course.service';
import { StudentService, StudentSearchFilters } from 'src/app/core/services/student.service';
import { ToastService } from 'src/app/core/services/toast.service';

import { EnrolledStudentsDataSource } from 'src/app/core/datasources/enrolled-students.datasource';

import { ConfirmDialog } from 'src/app/components/dialogs/confirm/confirm.component';
import { SelectableTableComponent } from 'src/app/components/selectable-table/selectable-table.component';

import { pictureTemplate, lastNameTemplate, teamTemplate } from './templates';

import { navHome, navCourses, nav } from '../professor.navdata';

@Component({
  selector: 'app-professor-students',
  templateUrl: './students.component.html',
  styleUrls: ['./students.component.css']
})
export class ProfessorStudentsComponent implements OnInit {
  courseCode: string;
  course$: Observable<Course>;
  navigationData$: Observable<Array<any>>;
  
  showSearch: boolean = false;
  studentMatches: any[] = [];
  searchSubject: Subject<string> = new Subject();
  searchSubscription: Subscription;

  enrolledStudentColumns = [    
    { name: 'id', label: 'ID', sortable: true },
    { name: 'picture', label: '', template: pictureTemplate },
    { name: 'firstName', label: 'First Name', sortable: true },
    { name: 'lastName', label: 'Last Name', sortable: true, template: lastNameTemplate },
    { name: 'teamName', label: 'Team', sortable: true, template: teamTemplate }
  ];
  selectedEnrolledStudents: Set<string> | 'all' = new Set<string>();
  enrolledStudentsDataSource: EnrolledStudentsDataSource;

  @ViewChild(SelectableTableComponent)
  studentsTable: SelectableTableComponent;
  
  @ViewChild('fileInput')
  fileInput: ElementRef;

  constructor(private route: ActivatedRoute, private courseService: CourseService, private studentService: StudentService,
      private dialog: MatDialog, private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.courseCode = this.route.snapshot.params.code;
    this.init();
  }
  init(): void {
    this.course$ = this.courseService.get(this.courseCode);
    this.enrolledStudentsDataSource = new EnrolledStudentsDataSource(this.courseService, this.courseCode);
    this.navigationData$ = this.course$.pipe(
      map(course => [navHome, navCourses, nav(course.name, `/professor/course/${course.code}`), nav('Students')])
    );

    this.searchSubject.pipe(
      debounceTime(250),
    ).subscribe(input => {
      if(this.searchSubscription)
        this.searchSubscription.unsubscribe();

      if(input.length > 0) {
        this.searchSubscription = this.studentService.search(input, new StudentSearchFilters({ excludeCourse: this.courseCode })).subscribe(students => {
          this.studentMatches = students.map(s => Object.assign(s, { username: `s${s.id}` }));
        });
      } else
        this.studentMatches = [];
    });
  }

  searchChanged(input: string) {
    this.searchSubject.next(input);
  }
  clearSearch() {
    this.showSearch = false;
    this.studentMatches = [];
  }
  searchResultSelected(id: number) {
    this.clearSearch();
    this.courseService.enroll(this.courseCode, id).subscribe(res => {
      if(res) {
        this.studentsTable.refresh();
        this.toastService.show({ type: 'success', text: 'Student enrolled successfully.' });
      } else
        this.toastService.show({ type: 'danger', text: 'An error occurred.' });
    })
  }
  searchCloseButtonClicked() {
    this.clearSearch();
  }

  selectedStudentsChanged(selected: Set<string> | 'all') {
    this.selectedEnrolledStudents = selected;
  }
  csvButtonClicked() {
    this.fileInput.nativeElement.click();
  }
  csvFileSelected(file: File) {
    this.courseService.enrollFromCSV(this.courseCode, file).subscribe(res => {
      if(res) {
        this.studentsTable.refresh();
        this.toastService.show({ type: 'success', text: 'CSV file imported successfully.' });
      } else
        this.toastService.show({ type: 'danger', text: 'An error occurred.' });
    });
  }
  unenrollAll() {
    this.dialog.open(ConfirmDialog, {
      data: {
        title: 'Unenroll students',
        message: `Are you sure you want to unenroll all ${this.enrolledStudentsDataSource.size} students of the course?`
      }
    }).afterClosed().subscribe(confirmed => {
      if(confirmed) {
        this.courseService.unenrollAll(this.courseCode).subscribe(res => {
          if(res) {
            this.studentsTable.refresh();
            this.toastService.show({ type: 'success', text: 'All students unenrolled successfully.' });
          } else
            this.toastService.show({ type: 'danger', text: 'An error occurred.' });
        });
      }
    });
  }
  unenrollStudents(studentIds: number[]) {
    this.dialog.open(ConfirmDialog, {
      data: {
        title: 'Unenroll students',
        message: `Are you sure you want to unenroll ${studentIds.length} selected students?`
      }
    }).afterClosed().subscribe(confirmed => {
      if(confirmed) {
        this.courseService.unenroll(this.courseCode, studentIds).subscribe(res => {
          if(res) {
            this.studentsTable.refresh();
            this.toastService.show({ type: 'success', text: 'Selected students unenrolled successfully.' });
          } else
            this.toastService.show({ type: 'danger', text: 'An error occurred.' });
        });
      }
    });
  }
  unenrollSelectedButtonClicked() {
    if(this.selectedEnrolledStudents == 'all') {
      this.unenrollAll();
    } else if(this.selectedEnrolledStudents.size) {
      this.unenrollStudents([...this.selectedEnrolledStudents].map(x => parseInt(x)));
    }
  }
}

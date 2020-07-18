import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Observable, Subscription, Subject } from 'rxjs';
import { debounceTime } from 'rxjs/operators';

import { Course } from 'src/app/core/models/course.model';
import { Student } from 'src/app/core/models/student.model';

import { CourseService } from 'src/app/core/services/course.service';
import { StudentService, StudentSearchFilters } from 'src/app/core/services/student.service';

import { numberValidator } from 'src/app/core/validators/core.validator';

import { navHome, navCourses, nav } from '../student.navdata';

@Component({
  selector: 'app-student-new-team',
  templateUrl: './new-team.component.html',
  styleUrls: ['./new-team.component.css']
})
export class StudentNewTeamComponent implements OnInit {
  courseCode: string;
  courseName: string;
  course$: Observable<Course>;
  navigationData: Array<any>|null = null;
  
  locked: boolean = false;
  members: Student[] = [];
  form = new FormGroup({
    name: new FormControl({ value: '', disabled: false }, [Validators.required]),
    timeout: new FormControl({ value: '', disabled: false }, [Validators.required, numberValidator, Validators.min(1), Validators.max(30)])
  });

  studentMatches: any[] = [];
  searchSubject: Subject<string> = new Subject();
  searchSubscription: Subscription;
  
  @ViewChild('studentInput')
  studentInputRef: ElementRef;

  constructor(private route: ActivatedRoute, private courseService: CourseService, private studentService: StudentService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseCode = params.code;
      this.course$ = this.courseService.get(this.courseCode);

      this.course$.subscribe(course => {
        this.courseName = course.name;
        this.navigationData = [navHome, navCourses, nav(course.name, `/student/course/${course.code}`), nav('Teams', `/student/course/${course.code}/teams`), nav('New')];
      });

      this.searchSubject.pipe(
        debounceTime(250),
      ).subscribe(input => {
        if(this.searchSubscription)
          this.searchSubscription.unsubscribe();

        if(input.length > 1) {
          this.searchSubscription = this.studentService.search(input, new StudentSearchFilters({ course: this.courseCode, excludeIds: this.members.map(m => m.id).join(), teamed: false })).subscribe(students => {
            this.studentMatches = students.map(s => Object.assign(s, { username: `s${s.id}` }));
          });
        } else
          this.studentMatches = [];
      });
    });
  }

  searchInputChanged(input: string) {
    this.searchSubject.next(input);
  }
  studentSelected(student: Student) {
    this.studentMatches = [];
    this.members = this.members.concat(student);
    this.studentInputRef.nativeElement.value = '';
  }
  studentDeselected(student: Student) {
    this.members = this.members.filter(s => s.id != student.id);
  }

  getFormErrorMessage() {
    if(this.form.hasError('maxmin'))
      return 'Maximum members number should be greater than or equal to minimum number.';
  }
  getNameErrorMessage() {
    if(this.form.get('name').hasError('required'))
      return 'You must enter the team name';
  }
  getTimeoutErrorMessage() {
    if(this.form.get('timeout').hasError('required'))
      return 'You must enter the proposal timeout';
    if(this.form.get('timeout').hasError('number'))
      return 'Please enter a number here';
    return this.form.get('timeout').hasError('min') || this.form.get('timeout').hasError('max') ? 'Proposal timeout must be in the range 1-30' : '';
  }
  createButtonClicked() {
  }
}

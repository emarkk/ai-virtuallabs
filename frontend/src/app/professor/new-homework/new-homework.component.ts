import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Observable } from 'rxjs';

import { Course } from 'src/app/core/models/course.model';

import { CourseService } from 'src/app/core/services/course.service';

import { navHome, navCourses, nav } from '../professor.navdata';

@Component({
  selector: 'app-professor-new-homework',
  templateUrl: './new-homework.component.html',
  styleUrls: ['./new-homework.component.css']
})
export class ProfessorNewHomeworkComponent implements OnInit {
  courseCode: string;
  course$: Observable<Course>;
  
  navigationData: Array<any>|null = null;
  
  locked: boolean = false;
  form = new FormGroup({
    title: new FormControl({ value: '', disabled: false }, [Validators.required, Validators.maxLength(28)]),
    dueDate: new FormControl({ value: '', disabled: false }, [Validators.required])
  });

  constructor(private route: ActivatedRoute, private courseService: CourseService) {
  }

  ngOnInit(): void {
    this.courseCode = this.route.snapshot.params.code;
    this.init();
  }
  init(): void {
    this.course$ = this.courseService.get(this.courseCode);
    this.course$.subscribe(course => {
      this.navigationData = [navHome, navCourses, nav(course.name, `/professor/course/${course.code}`), nav('Homeworks', `/professor/course/${course.code}/homeworks`), nav('New')];
    });
  }

  getTitleErrorMessage() {
    if(this.form.get('title').hasError('required'))
      return 'You must enter the title of the assignment';
    if(this.form.get('title').hasError('maxlength'))
      return 'Title must be no more than 28 characters long';
  }
  getDueDateErrorMessage() {
    if(this.form.get('dueDate').hasError('required'))
      return 'You must enter the due date';
  }
  createButtonClicked() {

  }
}

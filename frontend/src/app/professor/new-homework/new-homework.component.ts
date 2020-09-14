import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators'

import { Course } from 'src/app/core/models/course.model';

import { CourseService } from 'src/app/core/services/course.service';
import { HomeworkService } from 'src/app/core/services/homework.service';
import { ToastService } from 'src/app/core/services/toast.service';

import { navHome, navCourses, nav } from '../professor.navdata';

@Component({
  selector: 'app-professor-new-homework',
  templateUrl: './new-homework.component.html',
  styleUrls: ['./new-homework.component.css']
})
export class ProfessorNewHomeworkComponent implements OnInit {
  dueDateFilter = (d: Date | null): boolean => {
    return d && d > new Date();
  }

  courseCode: string;
  course$: Observable<Course>;
  
  navigationData$: Observable<Array<any>>;
  
  locked: boolean = false;
  form = new FormGroup({
    title: new FormControl({ value: '', disabled: false }, [Validators.required, Validators.maxLength(28)]),
    dueDate: new FormControl({ value: '', disabled: false }, [Validators.required]),
    file: new FormControl({ value: null, disabled: false }, [Validators.required])
  });

  constructor(private router: Router, private route: ActivatedRoute, private courseService: CourseService, private homeworkService: HomeworkService, private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.courseCode = this.route.snapshot.params.code;
    this.init();
  }
  init(): void {
    this.course$ = this.courseService.get(this.courseCode);
    this.navigationData$ = this.course$.pipe(
      map(course => [navHome, navCourses, nav(course.name, `/professor/course/${course.code}`), nav('Homeworks', `/professor/course/${course.code}/homeworks`), nav('New')])
    );
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
    if(this.form.get('dueDate').hasError('matDatepickerFilter'))
      return 'Due date must be greater than today\'s date';
  }
  getFileErrorMessage() {
    if(this.form.get('file').hasError('required'))
      return 'You must upload the assignment file';
  }
  lock() {
    this.locked = true;
    this.form.disable();
  }
  unlock() {
    this.locked = false;
    this.form.enable();
  }
  createButtonClicked() {
    if(this.form.invalid || this.locked)
      return;
    
    const title = this.form.get('title').value;
    const dueDate = this.form.get('dueDate').value.getTime() + ((23*60 + 59)*60 + 59)*1000;
    const file = this.form.get('file').value._files[0];

    this.lock();
    this.homeworkService.add(title, dueDate, file, this.courseCode).subscribe(res => {
      this.unlock();
      if(res) {
        this.router.navigate([`/professor/course/${this.courseCode}/homeworks`]);
        this.toastService.show({ type: 'success', text: 'Homework assignment created successfully.' });
      } else
        this.toastService.show({ type: 'danger', text: 'An error occurred.' });
    });
  }
}

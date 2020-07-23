import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';

import { Course } from 'src/app/core/models/course.model';
import { Student } from 'src/app/core/models/student.model';

import { AuthService } from 'src/app/core/services/auth.service';
import { StudentService } from 'src/app/core/services/student.service';

@Component({
  selector: 'app-student-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class StudentHomeComponent implements OnInit {
  me$: Observable<Student>;
  courses$: Observable<Course[]>;

  constructor(private authService: AuthService, private studentService: StudentService) {
  }

  ngOnInit(): void {
    this.me$ = this.studentService.get(this.authService.getId());
    this.courses$ = this.studentService.getCourses(this.authService.getId());
  }

}

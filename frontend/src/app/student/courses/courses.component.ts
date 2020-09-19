import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';

import { Course } from 'src/app/core/models/course.model';

import { StudentService } from 'src/app/core/services/student.service';
import { AuthService } from 'src/app/core/services/auth.service';

import { navHome, navCourses } from 'src/app/student/student.navdata';

@Component({
  selector: 'app-student-courses',
  templateUrl: './courses.component.html',
  styleUrls: ['./courses.component.css']
})
export class StudentCoursesComponent implements OnInit {
  courses$: Observable<Course[]>;
  
  navigationData: Array<any> = [navHome, navCourses];

  constructor(private authService: AuthService, private studentService: StudentService) {
  }

  ngOnInit(): void {
    this.courses$ = this.studentService.getCourses(this.authService.getId());
  }

}

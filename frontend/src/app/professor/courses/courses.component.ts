import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';

import { Course } from 'src/app/core/models/course.model';
import { ProfessorService } from 'src/app/core/services/professor.service';
import { AuthService } from 'src/app/core/services/auth.service';

import { navHome, navCourses } from '../professor.navdata';

@Component({
  selector: 'app-professor-courses',
  templateUrl: './courses.component.html',
  styleUrls: ['./courses.component.css']
})
export class ProfessorCoursesComponent implements OnInit {
  navigationData: Array<any> = [navHome, navCourses];
  courses$: Observable<Course[]>;

  constructor(private authService: AuthService, private professorService: ProfessorService) {
  }

  ngOnInit(): void {
    this.courses$ = this.professorService.getCourses(this.authService.getId());
  }

}

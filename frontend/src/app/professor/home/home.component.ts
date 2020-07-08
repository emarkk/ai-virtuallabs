import { Component, OnInit } from '@angular/core';

import { Course } from 'src/app/core/models/course.model';
import { AuthService } from 'src/app/core/services/auth.service';
import { ProfessorService } from 'src/app/core/services/professor.service';

@Component({
  selector: 'app-professor-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class ProfessorHomeComponent implements OnInit {
  courses: Course[] = [];

  constructor(private authService: AuthService, private professorService: ProfessorService) {
  }

  ngOnInit(): void {
    this.professorService.getCourses(this.authService.getUserData().id).subscribe(courses => this.courses = courses);
  }

}

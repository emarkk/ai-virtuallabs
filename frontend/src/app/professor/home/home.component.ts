import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';

import { Course } from 'src/app/core/models/course.model';
import { Professor } from 'src/app/core/models/professor.model';

import { AuthService } from 'src/app/core/services/auth.service';
import { ProfessorService } from 'src/app/core/services/professor.service';

@Component({
  selector: 'app-professor-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class ProfessorHomeComponent implements OnInit {
  me$: Observable<Professor>;
  courses$: Observable<Course[]>;

  constructor(private authService: AuthService, private professorService: ProfessorService) {
  }

  ngOnInit(): void {
    this.me$ = this.professorService.get(this.authService.getId());
    this.courses$ = this.professorService.getCourses(this.authService.getId());
  }

}

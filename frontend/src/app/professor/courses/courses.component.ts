import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { Course } from 'src/app/core/models/course.model';
import { navHome, navCourses } from '../professor.navdata';
import { CourseService } from 'src/app/core/services/course.service';
import { ProfessorService } from 'src/app/core/services/professor.service';
import { AuthService } from 'src/app/core/services/auth.service';

@Component({
  selector: 'app-professor-courses',
  templateUrl: './courses.component.html',
  styleUrls: ['./courses.component.css']
})
export class ProfessorCoursesComponent implements OnInit {
  navigationData: Array<any> = [
    navHome,
    navCourses
  ];
  courses$: Observable<Course[]>;
  insertionSuccess: boolean = false;

  constructor(private router: Router, private route: ActivatedRoute, private authService: AuthService,
      private courseService: CourseService, private professorService: ProfessorService) {
  }

  ngOnInit(): void {
    if(this.route.snapshot.params.insertionSuccess) {
      this.insertionSuccess = this.courseService.hasInsertedSuccessfully();
      if(!this.insertionSuccess)
        this.router.navigate(['/professor/courses']);
    }
    this.courses$ = this.professorService.getCourses(this.authService.getUserData().id);
  }

}

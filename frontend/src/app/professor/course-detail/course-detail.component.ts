import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subject, Subscription } from 'rxjs';

import { Course } from 'src/app/core/models/course.model';
import { Professor } from 'src/app/core/models/professor.model';
import { CourseService } from 'src/app/core/services/course.service';

import { navHome, navCourses, nav } from '../professor.navdata';

@Component({
  selector: 'app-professor-course-detail',
  templateUrl: './course-detail.component.html',
  styleUrls: ['./course-detail.component.css']
})
export class ProfessorCourseDetailComponent implements OnInit {
  courseCode: string;
  courseEnabled: boolean;
  course$: Observable<Course>;
  professors$: Observable<Professor[]>;
  navigationData: Array<any>|null = null;
  updatingStatus: boolean = false;

  showSearch: boolean = false;
  professorMatches: any[] = [];
  searchSubject: Subject<string> = new Subject;
  searchSubscription: Subscription;

  constructor(private router: Router, private route: ActivatedRoute, private courseService: CourseService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseCode = params.code;
      this.course$ = this.courseService.get(this.courseCode);
      this.professors$ = this.courseService.getProfessors(this.courseCode);

      this.course$.subscribe(course => {
        this.courseEnabled = course.enabled;
        this.navigationData = [navHome, navCourses, nav(course.name, '/professor/course/' + course.code)];
      });
    });
  }

  deleteButtonClicked() {
    if(confirm('Are you sure?')) {
      this.courseService.delete(this.courseCode).subscribe(res => {
        if(res)
          this.router.navigate(['/professor/courses']);
      });
    }
  }
  statusButtonClicked() {
    this.updatingStatus = true;
    if(this.courseEnabled) {
      this.courseService.disable(this.courseCode).subscribe(res => {
        this.updatingStatus = false;
        if(res)
          this.courseEnabled = false;
      });
    } else {
      this.courseService.enable(this.courseCode).subscribe(res => {
        this.updatingStatus = false;
        if(res)
          this.courseEnabled = true;
      });
    }
  }
  addCollaboratorButtonClicked() {
    this.showSearch = true;
  }
  searchChanged(input: string) {
    this.searchSubject.next(input);
  }
  searchResultSelected(id: number) {
    this.showSearch = false;
    this.professorMatches = [];
  }
  searchCloseButtonClicked() {
    this.showSearch = false;
    this.professorMatches = [];
  }
}

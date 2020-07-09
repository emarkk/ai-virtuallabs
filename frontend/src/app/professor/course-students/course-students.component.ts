import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, Subscription } from 'rxjs';

import { Course } from 'src/app/core/models/course.model';

import { CourseService } from 'src/app/core/services/course.service';
import { StudentService } from 'src/app/core/services/student.service';

import { FullscreenSearchComponent } from 'src/app/components/fullscreen-search/fullscreen-search.component';

import { navHome, navCourses, nav } from '../professor.navdata';

@Component({
  selector: 'app-professor-course-students',
  templateUrl: './course-students.component.html',
  styleUrls: ['./course-students.component.css']
})
export class ProfessorCourseStudentsComponent implements OnInit {
  courseCode: string;
  course$: Observable<Course>;
  navigationData: Array<any>|null = null;
  showSearch: boolean = false;

  searchSubscription: Subscription;
  
  @ViewChild(FullscreenSearchComponent)
  searchComponent: FullscreenSearchComponent;

  constructor(private route: ActivatedRoute, private courseService: CourseService, private studentService: StudentService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseCode = params.code;
      this.course$ = this.courseService.get(this.courseCode);

      this.course$.subscribe(course => {
        this.navigationData = [navHome, navCourses, nav(course.name, '/professor/course/' + course.code), nav('Students')];
      });
    });
  }

  searchChanged(input: string) {
    if(this.searchSubscription)
      this.searchSubscription.unsubscribe();

    this.searchSubscription = this.studentService.search(input).subscribe(students => {

    });
  }
}

import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, Subscription, Subject } from 'rxjs';
import { debounceTime } from 'rxjs/operators';

import { Course } from 'src/app/core/models/course.model';

import { CourseService } from 'src/app/core/services/course.service';
import { StudentService } from 'src/app/core/services/student.service';

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
  studentMatches: any[] = [];
  searchSubject: Subject<string> = new Subject;
  searchSubscription: Subscription;

  @ViewChild('fileInput')
  fileInput: ElementRef;

  constructor(private route: ActivatedRoute, private courseService: CourseService, private studentService: StudentService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseCode = params.code;
      this.course$ = this.courseService.get(this.courseCode);

      this.course$.subscribe(course => {
        this.navigationData = [navHome, navCourses, nav(course.name, '/professor/course/' + course.code), nav('Students')];
      });

      this.searchSubject.pipe(
        debounceTime(400),
      ).subscribe(input => {
        if(this.searchSubscription)
          this.searchSubscription.unsubscribe();

        if(input.length > 2) {
          this.searchSubscription = this.studentService.search(input).subscribe(students => {
            this.studentMatches = students.map(s => Object.assign(s, { username: 's' + s.id }));
          });
        } else
          this.studentMatches = [];
      });
    });
  }

  searchChanged(input: string) {
    this.searchSubject.next(input);
  }
  searchResultSelected(id: number) {
    this.showSearch = false;
    this.studentMatches = [];
  }
  searchCloseButtonClicked() {
    this.showSearch = false;
    this.studentMatches = [];
  }

  csvButtonClicked() {
    this.fileInput.nativeElement.click();
  }
}

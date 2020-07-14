import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subject, Subscription, BehaviorSubject } from 'rxjs';
import { debounceTime, switchMap } from 'rxjs/operators';

import { Course } from 'src/app/core/models/course.model';
import { Professor } from 'src/app/core/models/professor.model';
import { CourseService } from 'src/app/core/services/course.service';

import { ConfirmDialog } from 'src/app/components/dialogs/confirm/confirm.component';

import { navHome, navCourses, nav } from '../professor.navdata';
import { ProfessorService } from 'src/app/core/services/professor.service';
import { ToastService } from 'src/app/core/services/toast.service';

@Component({
  selector: 'app-professor-course-detail',
  templateUrl: './course-detail.component.html',
  styleUrls: ['./course-detail.component.css']
})
export class ProfessorCourseDetailComponent implements OnInit {
  courseCode: string;
  courseName: string;
  courseEnabled: boolean;
  course$: Observable<Course>;
  navigationData: Array<any>|null = null;
  updatingStatus: boolean = false;
  
  professorsRefreshToken = new BehaviorSubject(undefined);
  professors$: Observable<Professor[]>;

  showSearch: boolean = false;
  professorMatches: any[] = [];
  searchSubject: Subject<string> = new Subject();
  searchSubscription: Subscription;

  constructor(private router: Router, private route: ActivatedRoute, private courseService: CourseService,
      private professorService: ProfessorService, private dialog: MatDialog, private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseCode = params.code;
      this.course$ = this.courseService.get(this.courseCode);
      this.professors$ = this.professorsRefreshToken.pipe(
        switchMap(() => this.courseService.getProfessors(this.courseCode))
      );

      this.course$.subscribe(course => {
        this.courseName = course.name;
        this.courseEnabled = course.enabled;
        this.navigationData = [navHome, navCourses, nav(course.name, `/professor/course/${course.code}`)];
      });
      
      this.searchSubject.pipe(
        debounceTime(400),
      ).subscribe(input => {
        if(this.searchSubscription)
          this.searchSubscription.unsubscribe();

        if(input.length > 1) {
          this.searchSubscription = this.professorService.search(input, this.courseCode).subscribe(professors => {
            this.professorMatches = professors.map(s => Object.assign(s, { username: `d${s.id}` }));
          });
        } else
          this.professorMatches = [];
      });
    });
  }

  searchChanged(input: string) {
    this.searchSubject.next(input);
  }
  clearSearch() {
    this.showSearch = false;
    this.professorMatches = [];
  }
  searchResultSelected(id: number) {
    this.clearSearch();
    this.courseService.addProfessor(this.courseCode, id).subscribe(res => {
      if(res) {
        this.toastService.show({ type: 'success', text: 'Collaborator added successfully.' });
        this.professorsRefreshToken.next(undefined);
      }
    });
  }
  searchCloseButtonClicked() {
    this.clearSearch();
  }

  deleteButtonClicked() {
    this.dialog.open(ConfirmDialog, {
      data: {
        title: 'Delete course',
        message: `Are you sure you want to delete course "${this.courseName}"?`
      }
    }).afterClosed().subscribe(confirmed => {
      if(confirmed) {
        this.courseService.delete(this.courseCode).subscribe(res => {
          if(res) {
            this.router.navigate(['/professor/courses']);
            this.toastService.show({ type: 'success', text: 'Course deleted successfully.' });
          } else 
            this.toastService.show({ type: 'danger', text: 'An error occurred.' });
        });
      }
    });
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
}

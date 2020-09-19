import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject, combineLatest, forkJoin, Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

import { APIResult } from 'src/app/core/models/api-result.model';
import { Course } from 'src/app/core/models/course.model';
import { Homework } from 'src/app/core/models/homework.model';
import { HomeworkAction, HomeworkActionType } from 'src/app/core/models/homework-action.model';

import { AuthService } from 'src/app/core/services/auth.service';
import { CourseService } from 'src/app/core/services/course.service';
import { HomeworkService } from 'src/app/core/services/homework.service';
import { ToastService } from 'src/app/core/services/toast.service';

import { ImageDialog } from 'src/app/components/dialogs/image/image.component';

import { navHome, navCourses, nav } from '../student.navdata';

@Component({
  selector: 'app-student-homework-detail',
  templateUrl: './homework-detail.component.html',
  styleUrls: ['./homework-detail.component.css']
})
export class StudentHomeworkDetailComponent implements OnInit {
  Math = window.Math;
  ActionType = HomeworkActionType;

  courseCode: string;
  homeworkId: number;

  course$: Observable<Course>;
  homework$: Observable<Homework>;
  homeworkText: string;
  
  homeworkActions$: Observable<HomeworkAction[]>;
  homeworkActionsRefreshToken = new BehaviorSubject(undefined);
  
  imageDialogRef: MatDialogRef<ImageDialog> = null;

  navigationData$: Observable<Array<any>>;

  @ViewChild('fileDownload')
  fileDownload: ElementRef;

  @ViewChild('fileUpload')
  fileUpload: ElementRef;

  constructor(private router: Router, private route: ActivatedRoute, private authService: AuthService, private courseService: CourseService,
      private homeworkService: HomeworkService, private toastService: ToastService, private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.courseCode = this.route.snapshot.params.code;
    this.homeworkId = this.route.snapshot.params.id;
    this.init();
  }
  init(): void {
    this.course$ = this.courseService.get(this.courseCode);
    this.homework$ = this.homeworkService.get(this.homeworkId);
    this.homeworkActions$ = this.homeworkActionsRefreshToken.pipe(
      switchMap(() => this.homeworkService.getStudentActions(this.homeworkId, this.authService.getId()))
    );
    this.navigationData$ = forkJoin([this.course$, this.homework$]).pipe(
      map(([course, homework]) => [navHome, navCourses, nav(course.name, `/student/course/${course.code}`), nav('Homeworks', `/student/course/${course.code}/homeworks`), nav(homework.title)])
    );

    combineLatest([this.route.queryParams, this.homeworkActions$]).subscribe(([queryParams, homeworkActions]) => {
      if(queryParams.action && homeworkActions.some(a => a.id == queryParams.action)) {
        homeworkActions.find(a => a.id == queryParams.action).resource$.subscribe(resource => {
          this.imageDialogRef = this.dialog.open(ImageDialog, {
            data: {
              imageUrl: resource
            }
          });
          this.imageDialogRef.afterClosed().subscribe(_ => {
            this.router.navigate([]);
          });
        });
      } else if(this.imageDialogRef)
        this.imageDialogRef.close();
    });
  }

  downloadAssignmentText(): void {
    if(this.homeworkText) {
      this.fileDownload.nativeElement.click();
      return;
    }

    this.homeworkService.getText(this.homeworkId).subscribe(homeworkText => {
      this.homeworkText = homeworkText;
      this.homeworkActionsRefreshToken.next(undefined);
      setTimeout(() => {
        this.fileDownload.nativeElement.click();
      }, 150);
    });
  }
  submitHomeworkSolution(): void {
    this.fileUpload.nativeElement.click();
  }
  homeworkSolutionSelected(file: File): void {
    this.homeworkService.submitSolution(this.homeworkId, file).subscribe((res: APIResult) => {
      if(res.ok) {
        this.homeworkActionsRefreshToken.next(undefined);
        this.toastService.show({ type: 'success', text: 'Assignment solution submitted successfully.' });
      } else
        this.toastService.show({ type: 'danger', text: res.errorMessage });
    });
  }
}

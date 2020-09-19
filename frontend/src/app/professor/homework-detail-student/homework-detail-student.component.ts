import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject, combineLatest, forkJoin, Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

import { APIResult } from 'src/app/core/models/api-result.model';
import { Course } from 'src/app/core/models/course.model';
import { Homework } from 'src/app/core/models/homework.model';
import { Student } from 'src/app/core/models/student.model';
import { HomeworkAction, HomeworkActionType } from 'src/app/core/models/homework-action.model';

import { CourseService } from 'src/app/core/services/course.service';
import { HomeworkService } from 'src/app/core/services/homework.service';
import { StudentService } from 'src/app/core/services/student.service';
import { ToastService } from 'src/app/core/services/toast.service';

import { ImageDialog } from 'src/app/components/dialogs/image/image.component';
import { HomeworkReviewDialog } from 'src/app/components/dialogs/homework-review/homework-review.component';

import { navHome, navCourses, nav } from '../professor.navdata';

@Component({
  selector: 'app-professor-homework-detail-student',
  templateUrl: './homework-detail-student.component.html',
  styleUrls: ['./homework-detail-student.component.css']
})
export class ProfessorHomeworkDetailStudentComponent implements OnInit {
  Math = window.Math;
  ActionType = HomeworkActionType;

  courseCode: string;
  homeworkId: number;
  studentId: number;

  course$: Observable<Course>;
  homework$: Observable<Homework>;
  student$: Observable<Student>;
  homeworkText: string;
  
  homeworkActions$: Observable<HomeworkAction[]>;
  homeworkActionsRefreshToken = new BehaviorSubject(undefined);
  
  imageDialogRef: MatDialogRef<ImageDialog> = null;
  reviewDialogRef: MatDialogRef<HomeworkReviewDialog> = null;

  navigationData$: Observable<Array<any>>;

  @ViewChild('fileDownload')
  fileDownload: ElementRef;

  @ViewChild('fileUpload')
  fileUpload: ElementRef;

  constructor(private router: Router, private route: ActivatedRoute, private courseService: CourseService, private homeworkService: HomeworkService,
      private studentService: StudentService, private toastService: ToastService, private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.courseCode = this.route.snapshot.params.code;
    this.homeworkId = this.route.snapshot.params.id;
    this.studentId = this.route.snapshot.params.studentId;
    this.init();
  }
  init(): void {
    this.course$ = this.courseService.get(this.courseCode);
    this.homework$ = this.homeworkService.get(this.homeworkId);
    this.student$ = this.studentService.get(this.studentId);
    this.homeworkActions$ = this.homeworkActionsRefreshToken.pipe(
      switchMap(() => this.homeworkService.getStudentActions(this.homeworkId, this.studentId))
    );
    this.navigationData$ = forkJoin([this.course$, this.homework$, this.student$]).pipe(
      map(([course, homework, student]) => [
        navHome,
        navCourses,
        nav(course.name, `/professor/course/${course.code}`),
        nav('Homeworks', `/professor/course/${course.code}/homeworks`),
        nav(homework.title, `/professor/course/${course.code}/homework/${homework.id}`),
        nav(`${student.firstName} ${student.lastName.toUpperCase()} (${student.id})`)
      ])
    );
    
    combineLatest([this.route.queryParams, this.homeworkService.getText(this.homeworkId), this.homeworkActions$]).subscribe(([queryParams, homeworkText, homeworkActions]) => {
      this.homeworkText = homeworkText;

      if((queryParams.action && homeworkActions.some(a => a.id == queryParams.action)) || (queryParams.show == 'text')) {
        if(queryParams.show == 'text') {
          this.imageDialogRef = this.dialog.open(ImageDialog, { data: { imageUrl: this.homeworkText } });
          this.imageDialogRef.afterClosed().subscribe(_ => this.router.navigate([]));
        } else {
          homeworkActions.find(a => a.id == queryParams.action).resource$.subscribe(resource => {
            this.imageDialogRef = this.dialog.open(ImageDialog, { data: { imageUrl: resource } });
            this.imageDialogRef.afterClosed().subscribe(_ => this.router.navigate([]));
          });
        }
      } else if(this.imageDialogRef)
        this.imageDialogRef.close();

      if(queryParams.review && homeworkActions.some(a => a.id == queryParams.review)) {
        this.reviewDialogRef = this.dialog.open(HomeworkReviewDialog, { data: {
          homeworkId: this.homeworkId,
          actionId: queryParams.review
        } });
        this.reviewDialogRef.afterClosed().subscribe(res => {
          if(res instanceof APIResult) {
            if(res.ok) {
              this.homeworkActionsRefreshToken.next(undefined);
              this.toastService.show({ type: 'success', text: 'Review uploaded successfully.' });
            } else if(res.error)
              this.toastService.show({ type: 'danger', text: res.errorMessage });
          }
            
          this.router.navigate([]);
        });
      } else if(this.reviewDialogRef)
        this.reviewDialogRef.close();
    });
  }

}
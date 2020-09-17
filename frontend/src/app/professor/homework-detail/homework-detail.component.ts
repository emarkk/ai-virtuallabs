import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { combineLatest, forkJoin, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Course } from 'src/app/core/models/course.model';
import { Homework } from 'src/app/core/models/homework.model';

import { CourseService } from 'src/app/core/services/course.service';
import { HomeworkService } from 'src/app/core/services/homework.service';

import { HomeworkOverviewDataSource } from 'src/app/core/datasources/homework-overview.datasource';

import { ImageDialog } from 'src/app/components/dialogs/image/image.component';

import { historyTemplate, studentFieldsTemplate, studentLastNameTemplate, timestampTemplate } from './templates';

import { navHome, navCourses, nav } from '../professor.navdata';

@Component({
  selector: 'app-professor-homework-detail',
  templateUrl: './homework-detail.component.html',
  styleUrls: ['./homework-detail.component.css']
})
export class ProfessorHomeworkDetailComponent implements OnInit {
  courseCode: string;
  homeworkId: number;

  course$: Observable<Course>;
  homework$: Observable<Homework>;
  homeworkText: string;
  
  homeworkOverviewColumns = [    
    { name: 'id', label: 'ID', template: studentFieldsTemplate('id') },
    { name: 'picture', label: '', special: '$PICTURE$', picture: action => action.student },
    { name: 'firstName', label: 'First Name', template: studentFieldsTemplate('firstName') },
    { name: 'lastName', label: 'Last Name', template: studentLastNameTemplate },
    { name: 'status', label: 'Status' },
    { name: 'timestamp', label: 'Timestamp', template: timestampTemplate },
    { name: 'actions', label: '' },
  ];
  homeworkOverviewDataSource: HomeworkOverviewDataSource;
  
  imageDialogRef: MatDialogRef<ImageDialog> = null;
  
  navigationData$: Observable<Array<any>>;

  constructor(private router: Router, private route: ActivatedRoute, private courseService: CourseService, private homeworkService: HomeworkService, private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.courseCode = this.route.snapshot.params.code;
    this.homeworkId = this.route.snapshot.params.id;
    this.init();
  }
  init(): void {
    this.course$ = this.courseService.get(this.courseCode);
    this.homework$ = this.homeworkService.get(this.homeworkId);
    this.homeworkOverviewDataSource = new HomeworkOverviewDataSource(this.homeworkService, this.homeworkId);
    this.navigationData$ = forkJoin([this.course$, this.homework$]).pipe(
      map(([course, homework]) => [navHome, navCourses, nav(course.name, `/professor/course/${course.code}`), nav('Homeworks', `/professor/course/${course.code}/homeworks`), nav(homework.title)])
    );

    combineLatest([this.route.queryParams, this.homeworkService.getText(this.homeworkId)]).subscribe(([queryParams, homeworkText]) => {
      this.homeworkText = homeworkText;
      if(queryParams.show == 'text') {
        this.imageDialogRef = this.dialog.open(ImageDialog, {
          data: {
            imageUrl: this.homeworkText
          }
        });
        this.imageDialogRef.afterClosed().subscribe(_ => {
          this.router.navigate([]);
        });
      } else if(this.imageDialogRef)
        this.imageDialogRef.close();
    });
  }

}
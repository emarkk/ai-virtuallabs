import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subject, Subscription, BehaviorSubject, combineLatest } from 'rxjs';
import { debounceTime, switchMap } from 'rxjs/operators';

import { Course } from 'src/app/core/models/course.model';
import { Professor } from 'src/app/core/models/professor.model';
import { Student } from 'src/app/core/models/student.model';
import { Page } from 'src/app/core/models/page.model';
import { VmModel } from 'src/app/core/models/vmmodel.model';

import { CourseService } from 'src/app/core/services/course.service';
import { ProfessorService, ProfessorSearchFilters } from 'src/app/core/services/professor.service';
import { ToastService } from 'src/app/core/services/toast.service';
import { VmService } from 'src/app/core/services/vm.service';

import { ConfirmDialog } from 'src/app/components/dialogs/confirm/confirm.component';
import { VmModelDialog } from 'src/app/components/dialogs/vm-model/vm-model.component';

import { navHome, navCourses, nav } from '../professor.navdata';

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
  
  professors$: Observable<Professor[]>;
  professorsRefreshToken = new BehaviorSubject(undefined);

  studentsPreview$: Observable<Page<Student>>;

  vmModel$: Observable<VmModel>;
  vmModelRefreshToken = new BehaviorSubject(undefined);
  vmModelDialogRef: MatDialogRef<VmModelDialog> = null;

  showSearch: boolean = false;
  professorMatches: any[] = [];
  searchSubject: Subject<string> = new Subject();
  searchSubscription: Subscription;

  constructor(private router: Router, private route: ActivatedRoute, private courseService: CourseService, private professorService: ProfessorService,
    private vmService: VmService, private dialog: MatDialog, private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseCode = params.code;
      this.course$ = this.courseService.get(this.courseCode);
      this.professors$ = this.professorsRefreshToken.pipe(
        switchMap(() => this.courseService.getProfessors(this.courseCode))
      );
      this.studentsPreview$ = this.courseService.getStudents(this.courseCode, null, null, 0, 30);
      this.vmModel$ = this.vmModelRefreshToken.pipe(
        switchMap(() => this.courseService.getVmModel(this.courseCode)),
        switchMap(x => this.vmService.getModel(x.id))
      );

      combineLatest([this.route.queryParams, this.vmModel$]).subscribe(([queryParams, vmModel]) => {
        if(queryParams.edit == 'vm-model') {
          this.vmModelDialogRef = this.dialog.open(VmModelDialog, {
            data: {
              id: vmModel.id,
              name: vmModel.name,
              configuration: vmModel.configuration,
              courseCode: this.courseCode,
              courseName: this.courseName
            }
          });
          this.vmModelDialogRef.afterClosed().subscribe(res => {
            if(res) {
              this.vmModelRefreshToken.next(undefined);
              this.toastService.show({ type: 'success', text: 'VM model information saved successfully.' });
            } else if(res === false)
              this.toastService.show({ type: 'danger', text: 'An error occurred.' });
              
            this.router.navigate([]);
          });
        } else if(this.vmModelDialogRef)
          this.vmModelDialogRef.close();
      });

      this.course$.subscribe(course => {
        this.courseName = course.name;
        this.courseEnabled = course.enabled;
        this.navigationData = [navHome, navCourses, nav(course.name, `/professor/course/${course.code}`)];
      });
      
      this.searchSubject.pipe(
        debounceTime(250),
      ).subscribe(input => {
        if(this.searchSubscription)
          this.searchSubscription.unsubscribe();

        if(input.length > 0) {
          this.searchSubscription = this.professorService.search(input, new ProfessorSearchFilters({ excludeCourse: this.courseCode })).subscribe(professors => {
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
        this.professorsRefreshToken.next(undefined);
        this.toastService.show({ type: 'success', text: 'Collaborator added successfully.' });
      } else 
        this.toastService.show({ type: 'danger', text: 'An error occurred.' });
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
    this.courseService.setEnabled(this.courseCode, !this.courseEnabled).subscribe(res => {
      this.updatingStatus = false;
      if(res)
        this.courseEnabled = this.courseEnabled ? false : true;
      else
        this.toastService.show({ type: 'danger', text: 'An error occurred.' });
    });
  }
  addCollaboratorButtonClicked() {
    this.showSearch = true;
  }
  vmModelLinkClicked() {
    this.router.navigate([`professor/course/${this.courseCode}`], { queryParams: { edit: 'vm-model' } });
  }
}

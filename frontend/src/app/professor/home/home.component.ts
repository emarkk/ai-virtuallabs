import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

import { Course } from 'src/app/core/models/course.model';
import { Professor } from 'src/app/core/models/professor.model';

import { AuthService } from 'src/app/core/services/auth.service';
import { ProfessorService } from 'src/app/core/services/professor.service';
import { ToastService } from 'src/app/core/services/toast.service';

@Component({
  selector: 'app-professor-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class ProfessorHomeComponent implements OnInit {
  me$: Observable<Professor>;
  courses$: Observable<Course[]>;
  
  profilePictureRefreshToken = new BehaviorSubject(undefined);
  
  @ViewChild('fileInput')
  fileInput: ElementRef;

  constructor(private authService: AuthService, private professorService: ProfessorService, private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.me$ = this.profilePictureRefreshToken.pipe(
      switchMap(() => this.professorService.get(this.authService.getId()))
    );
    this.courses$ = this.professorService.getCourses(this.authService.getId());
  }

  editPictureClicked(): void {
    this.fileInput.nativeElement.click();
  }
  profilePictureSelected(file: File): void {
    this.professorService.setProfilePicture(this.authService.getId(), file).subscribe(res => {
      if(res) {
        this.profilePictureRefreshToken.next(undefined);
        this.toastService.show({ type: 'success', text: 'Profile picture updated successfully.' });
      } else
        this.toastService.show({ type: 'danger', text: 'An error occurred.' });
    });
  }
}

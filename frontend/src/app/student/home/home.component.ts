import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Observable } from 'rxjs';

import { Course } from 'src/app/core/models/course.model';
import { Student } from 'src/app/core/models/student.model';

import { AuthService } from 'src/app/core/services/auth.service';
import { StudentService } from 'src/app/core/services/student.service';
import { ToastService } from 'src/app/core/services/toast.service';

@Component({
  selector: 'app-student-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class StudentHomeComponent implements OnInit {
  me$: Observable<Student>;
  courses$: Observable<Course[]>;
  
  @ViewChild('fileInput')
  fileInput: ElementRef;

  constructor(private authService: AuthService, private studentService: StudentService, private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.me$ = this.studentService.get(this.authService.getId());
    this.courses$ = this.studentService.getCourses(this.authService.getId());
  }

  editPictureClicked(): void {
    this.fileInput.nativeElement.click();
  }
  profilePictureSelected(file: File): void {
    this.studentService.setProfilePicture(this.authService.getId(), file).subscribe(res => {
      if(res)
        this.toastService.show({ type: 'success', text: 'Profile picture updated successfully.' });
      else
        this.toastService.show({ type: 'danger', text: 'An error occurred.' });
    });
  }
}

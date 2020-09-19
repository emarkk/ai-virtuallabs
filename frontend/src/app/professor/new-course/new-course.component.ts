import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';

import { APIResult } from 'src/app/core/models/api-result.model';

import { CourseService } from 'src/app/core/services/course.service';
import { ToastService } from 'src/app/core/services/toast.service';

import { CourseFormComponent } from 'src/app/components/course-form/course-form.component';

import { navHome, navCourses, nav } from '../professor.navdata';

@Component({
  selector: 'app-professor-new-course',
  templateUrl: './new-course.component.html',
  styleUrls: ['./new-course.component.css']
})
export class ProfessorNewCourseComponent implements OnInit {
  navigationData: Array<any> = [navHome, navCourses, nav('New')];
  
  @ViewChild(CourseFormComponent)
  formComponent: CourseFormComponent;

  constructor(private router: Router, private courseService: CourseService, private toastService: ToastService) {
  }

  ngOnInit(): void {

  }

  saveCourse(courseData) {
    this.formComponent.lock();
    const { code, name, acronym, minTeamMembers, maxTeamMembers, enabled } = courseData;
    this.courseService.add(code, name, acronym, minTeamMembers, maxTeamMembers, enabled).subscribe((res: APIResult) => {
      this.formComponent.unlock();
      if(res.ok) {
        this.router.navigate(['/professor/courses']);
        this.toastService.show({ type: 'success', text: 'New course created successfully.' });
      } else
        this.toastService.show({ type: 'danger', text: res.errorMessage });
    });
  }

}

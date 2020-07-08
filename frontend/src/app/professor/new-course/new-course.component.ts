import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';

import { CourseService } from 'src/app/core/services/course.service';
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

  constructor(private router: Router, private courseService: CourseService) {
  }

  ngOnInit(): void {

  }

  saveCourse(courseData) {
    this.formComponent.lock();
    const { code, name, acronym, minTeamMembers, maxTeamMembers, enabled } = courseData;
    this.courseService.add(code, name, acronym, minTeamMembers, maxTeamMembers, enabled).subscribe(res => {
      this.formComponent.unlock();
      if(res) {
        this.courseService.hasInsertedSuccessfully();
        this.router.navigate(['/professor/courses?insertionSuccess']);
      } else
        this.formComponent.form.setErrors({ error: true });
    });
  }

}

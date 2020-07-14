import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';

import { Course } from 'src/app/core/models/course.model';

import { CourseService } from 'src/app/core/services/course.service';
import { ToastService } from 'src/app/core/services/toast.service';

import { CourseFormComponent } from 'src/app/components/course-form/course-form.component';

import { navHome, navCourses, nav } from '../professor.navdata';

@Component({
  selector: 'app-professor-edit-course',
  templateUrl: './edit-course.component.html',
  styleUrls: ['./edit-course.component.css']
})
export class ProfessorEditCourseComponent implements OnInit {
  courseCode: string;
  course$: Observable<Course>;
  navigationData: Array<any>|null = null;
  
  @ViewChild(CourseFormComponent)
  formComponent: CourseFormComponent;

  constructor(private router: Router, private route: ActivatedRoute, private courseService: CourseService, private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseCode = params.code;
      this.course$ = this.courseService.get(this.courseCode);

      this.course$.subscribe(course => {
        this.navigationData = [navHome, navCourses, nav(course.name, `/professor/course/${course.code}`), nav('Edit')];
      });
    });
  }

  saveCourse(courseData) {
    this.formComponent.lock();
    const { name, acronym, minTeamMembers, maxTeamMembers, enabled } = courseData;
    this.courseService.update(this.courseCode, name, acronym, minTeamMembers, maxTeamMembers, enabled).subscribe(res => {
      this.formComponent.unlock();
      if(res) {
        this.router.navigate(['/professor/courses']);
        this.toastService.show({ type: 'success', text: 'Course information updated successfully.' });
      } else
        this.toastService.show({ type: 'danger', text: 'An error occurred.' });
    });
  }

}

import { Component, OnInit } from '@angular/core';

import { Course } from 'src/app/core/models/course.model';
import { navHome, navCourses, nav } from '../professor.navdata';
import { ActivatedRoute } from '@angular/router';
import { CourseService } from 'src/app/core/services/course.service';

@Component({
  selector: 'app-professor-course-detail',
  templateUrl: './course-detail.component.html',
  styleUrls: ['./course-detail.component.css']
})
export class ProfessorCourseDetailComponent implements OnInit {
  navigationData: Array<any> = [
    navHome,
    navCourses
  ];
  course: Course;

  constructor(private route: ActivatedRoute, private courseService: CourseService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseService.get(params.code).subscribe(course => {
        this.course = course;
        this.navigationData = this.navigationData.concat(nav(course.name));
      });
   });
  }

}

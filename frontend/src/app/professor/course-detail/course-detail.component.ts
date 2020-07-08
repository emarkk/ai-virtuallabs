import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Course } from 'src/app/core/models/course.model';
import { Professor } from 'src/app/core/models/professor.model';
import { CourseService } from 'src/app/core/services/course.service';

import { navHome, navCourses, nav } from '../professor.navdata';

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
  professors: Professor[] = [];

  constructor(private route: ActivatedRoute, private courseService: CourseService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseService.get(params.code).subscribe(course => {
        this.course = course;
        this.navigationData = this.navigationData.concat(nav(course.name));
      });
      this.courseService.getProfessors(params.code).subscribe(professors => this.professors = professors);
   });
  }

}

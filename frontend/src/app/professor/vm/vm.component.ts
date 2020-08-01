import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { Course } from 'src/app/core/models/course.model';

import { CourseService } from 'src/app/core/services/course.service';

import { navHome, navCourses, nav } from '../professor.navdata';

@Component({
  selector: 'app-student-vm',
  templateUrl: './vm.component.html',
  styleUrls: ['./vm.component.css']
})
export class ProfessorVmComponent implements OnInit {
  courseCode: string;
  courseName: string;
  course$: Observable<Course>;

  vmId: number;

  navigationData: Array<any>|null = null;

  constructor(private route: ActivatedRoute, private courseService: CourseService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseCode = params.code;
      this.course$ = this.courseService.get(this.courseCode);

      this.vmId = params.id;
      
      this.course$.subscribe(course => {
        this.courseName = course.name;
        this.navigationData = [navHome, navCourses, nav(course.name, `/professor/course/${course.code}`), nav('VMs', `/professor/course/${course.code}/vms`)];
      });
    });
  }

}

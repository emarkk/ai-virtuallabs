import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

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
  vmId: number;
  course$: Observable<Course>;

  navigationData$: Observable<Array<any>>;

  constructor(private route: ActivatedRoute, private courseService: CourseService) {
  }

  ngOnInit(): void {
    this.courseCode = this.route.snapshot.params.code;
    this.vmId = this.route.snapshot.params.id;
    this.init();
  }
  init(): void {
    this.course$ = this.courseService.get(this.courseCode);
    this.navigationData$ = this.course$.pipe(
      map(course => [navHome, navCourses, nav(course.name, `/professor/course/${course.code}`), nav('VMs', `/professor/course/${course.code}/vms`)])
    );
  }

}

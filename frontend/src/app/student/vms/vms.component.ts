import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { Course } from 'src/app/core/models/course.model';
import { Team } from 'src/app/core/models/team.model';

import { CourseService } from 'src/app/core/services/course.service';
import { StudentService } from 'src/app/core/services/student.service';
import { AuthService } from 'src/app/core/services/auth.service';

import { navHome, navCourses, nav } from '../student.navdata';

@Component({
  selector: 'app-student-vms',
  templateUrl: './vms.component.html',
  styleUrls: ['./vms.component.css']
})
export class StudentVmsComponent implements OnInit {
  courseCode: string;
  courseName: string;
  course$: Observable<Course>;
  team$: Observable<Team>;
  
  navigationData: Array<any>|null = null;

  constructor(private route: ActivatedRoute, private courseService: CourseService, private studentService: StudentService, private authService: AuthService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseCode = params.code;
      this.course$ = this.courseService.get(this.courseCode);
      this.team$ = this.studentService.getJoinedTeamForCourse(this.authService.getId(), this.courseCode);
      
      this.course$.subscribe(course => {
        this.courseName = course.name;
        this.navigationData = [navHome, navCourses, nav(course.name, `/student/course/${course.code}`), nav('VMs')];
      });
    });
  }

}

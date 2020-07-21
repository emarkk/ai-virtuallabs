import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { Course } from 'src/app/core/models/course.model';
import { Professor } from 'src/app/core/models/professor.model';
import { Team, TeamStatus } from 'src/app/core/models/team.model';

import { CourseService } from 'src/app/core/services/course.service';
import { StudentService } from 'src/app/core/services/student.service';
import { AuthService } from 'src/app/core/services/auth.service';

import { navHome, navCourses, nav } from '../student.navdata';

@Component({
  selector: 'app-student-course-detail',
  templateUrl: './course-detail.component.html',
  styleUrls: ['./course-detail.component.css']
})
export class StudentCourseDetailComponent implements OnInit {
  courseCode: string;
  courseName: string;
  courseEnabled: boolean;
  course$: Observable<Course>;
  navigationData: Array<any>|null = null;

  professors$: Observable<Professor[]>;
  teams$: Observable<Team[]>;

  team: Team;
  teamInvitations: Team[];

  constructor(private route: ActivatedRoute, private authService: AuthService, private courseService: CourseService, private studentService: StudentService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseCode = params.code;
      this.course$ = this.courseService.get(this.courseCode);
      this.professors$ = this.courseService.getProfessors(this.courseCode);
      this.teams$ = this.studentService.getTeamsForCourse(this.authService.getUserData().id, this.courseCode);

      this.course$.subscribe(course => {
        this.courseName = course.name;
        this.courseEnabled = course.enabled;
        this.navigationData = [navHome, navCourses, nav(course.name, `/professor/course/${course.code}`)];
      });
      this.teams$.subscribe(teams => {
        this.team = teams.find(t => t.status == TeamStatus.COMPLETE);
      });
    });
  }

}

import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { fromEvent, Observable } from 'rxjs';
import { map, throttleTime } from 'rxjs/operators';

import { Course } from 'src/app/core/models/course.model';
import { Team } from 'src/app/core/models/team.model';
import { Professor } from 'src/app/core/models/professor.model';
import { Student } from 'src/app/core/models/student.model';

import { VmScreenSignal } from 'src/app/core/models/signals/vm-screen.signal';

import { AuthService } from 'src/app/core/services/auth.service';
import { CourseService } from 'src/app/core/services/course.service';
import { SignalObservable, SignalService } from 'src/app/core/services/signal.service';
import { StudentService } from 'src/app/core/services/student.service';

import { navHome, navCourses, nav } from '../student.navdata';

@Component({
  selector: 'app-student-vm',
  templateUrl: './vm.component.html',
  styleUrls: ['./vm.component.css']
})
export class StudentVmComponent implements OnInit, OnDestroy {
  courseCode: string;
  vmId: number;

  course$: Observable<Course>;
  team$: Observable<Team>;

  vmOnline: boolean = null;
  connectedProfessors: Professor[];
  connectedStudents: Student[];
  
  vmScreenUpdatesSignal: SignalObservable<VmScreenSignal>;

  navigationData$: Observable<Array<any>>;

  constructor(private route: ActivatedRoute, private authService: AuthService, private courseService: CourseService, private studentService: StudentService, private signalService: SignalService) {
  }

  ngOnInit(): void {
    this.courseCode = this.route.snapshot.params.code;
    this.vmId = this.route.snapshot.params.id;
    this.init();
  }
  init(): void {
    this.course$ = this.courseService.get(this.courseCode);
    this.team$ = this.studentService.getJoinedTeamForCourse(this.authService.getId(), this.courseCode);
    this.navigationData$ = this.course$.pipe(
      map(course => [navHome, navCourses, nav(course.name, `/student/course/${course.code}`), nav('VMs', `/student/course/${course.code}/vms`)])
    );
    
    this.signalService.vmScreenUpdates(this.vmId).subscribe(signal => {
      this.vmScreenUpdatesSignal = signal;
      this.vmScreenUpdatesSignal.data().subscribe(update => {
        if(update.online != null)
          this.vmOnline = update.online;
        if(update.connectedProfessors != null)
          this.connectedProfessors = update.connectedProfessors;
        if(update.connectedStudents != null)
          this.connectedStudents = update.connectedStudents;
      });
    });
  }
  ngOnDestroy(): void {
    if(this.vmScreenUpdatesSignal)
      this.vmScreenUpdatesSignal.unsubscribe();
  }

}

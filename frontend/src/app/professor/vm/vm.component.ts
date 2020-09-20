import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { fromEvent, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Course } from 'src/app/core/models/course.model';
import { Professor } from 'src/app/core/models/professor.model';

import { VmScreenSignal } from 'src/app/core/models/signals/vm-screen.signal';
import { Student } from 'src/app/core/models/student.model';

import { CourseService } from 'src/app/core/services/course.service';
import { SignalObservable, SignalService } from 'src/app/core/services/signal.service';

import { navHome, navCourses, nav } from '../professor.navdata';

@Component({
  selector: 'app-student-vm',
  templateUrl: './vm.component.html',
  styleUrls: ['./vm.component.css']
})
export class ProfessorVmComponent implements OnInit, AfterViewInit, OnDestroy {
  courseCode: string;
  vmId: number;

  course$: Observable<Course>;

  teamName: string = null;
  vmOnline: boolean = null;
  connectedProfessors: Professor[];
  connectedStudents: Student[];
  
  vmScreenUpdatesSignal: SignalObservable<VmScreenSignal>;

  navigationData$: Observable<Array<any>>;

  @ViewChild('screen')
  screen: ElementRef;

  constructor(private route: ActivatedRoute, private courseService: CourseService, private signalService: SignalService) {
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
    
    this.signalService.vmScreenUpdates(this.vmId).subscribe(signal => {
      this.vmScreenUpdatesSignal = signal;
      this.vmScreenUpdatesSignal.data().subscribe(update => {
        if(update.online != null)
          this.vmOnline = update.online;
        if(update.teamName != null)
          this.teamName = update.teamName;
        if(update.connectedProfessors != null)
          this.connectedProfessors = update.connectedProfessors;
        if(update.connectedStudents != null)
          this.connectedStudents = update.connectedStudents;
      });
    });
  }
  ngAfterViewInit() {
    /*fromEvent(this.screen.nativeElement, 'mousemove').pipe(debounceTime(125)).subscribe(e => {
      console.log(e);
    });*/
  }
  ngOnDestroy(): void {
    if(this.vmScreenUpdatesSignal)
      this.vmScreenUpdatesSignal.unsubscribe();
  }

}

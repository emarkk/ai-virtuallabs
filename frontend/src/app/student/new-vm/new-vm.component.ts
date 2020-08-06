import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, merge, of } from 'rxjs';
import { map, scan } from 'rxjs/operators';

import { Course } from 'src/app/core/models/course.model';
import { TeamStatus } from 'src/app/core/models/team.model';
import { VmModel } from 'src/app/core/models/vmmodel.model';
import { Vm } from 'src/app/core/models/vm.model';
import { TeamVmsResources } from 'src/app/core/models/team-vms-resources.model';

import { TeamVmsResourcesSignal, TeamVmsResourcesSignalUpdateType } from 'src/app/core/models/signals/team-vms-resources.signal';

import { CourseService } from 'src/app/core/services/course.service';
import { VmService } from 'src/app/core/services/vm.service';
import { ToastService } from 'src/app/core/services/toast.service';
import { StudentService } from 'src/app/core/services/student.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { TeamService } from 'src/app/core/services/team.service';
import { SignalService, SignalObservable } from 'src/app/core/services/signal.service';

import { VmFormComponent } from 'src/app/components/vm-form/vm-form.component';

import { navHome, navCourses, nav } from '../student.navdata';

@Component({
  selector: 'app-student-new-vm',
  templateUrl: './new-vm.component.html',
  styleUrls: ['./new-vm.component.css']
})
export class StudentNewVmComponent implements OnInit, OnDestroy {
  courseCode: string;
  courseName: string;
  course$: Observable<Course>;

  vmModel: VmModel;
  teamId: number;

  vmInfo: { vm: Observable<Vm>, resourcesUsed: Observable<TeamVmsResources>, resourcesLimits: Observable<TeamVmsResources> };
  teamVmsResourcesUpdatesSignal: SignalObservable<TeamVmsResourcesSignal>;

  navigationData: Array<any>|null = null;

  @ViewChild(VmFormComponent)
  formComponent: VmFormComponent;

  constructor(private router: Router, private route: ActivatedRoute, private authService: AuthService, private courseService: CourseService, private studentService: StudentService,
    private teamService: TeamService, private vmService: VmService, private signalService: SignalService, private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseCode = params.code;
      this.course$ = this.courseService.get(this.courseCode);
      
      this.courseService.getVmModel(this.courseCode).subscribe(model => {
        this.vmModel = model;
      });

      this.studentService.getTeamsForCourse(this.authService.getId(), this.courseCode).pipe(map(teams => teams.find(t => t.status == TeamStatus.COMPLETE))).subscribe(team => {
        if(team) {
          this.teamId = team.id;

          this.signalService.teamVmsResourcesUpdates(this.teamId).subscribe(signal => {
            this.teamVmsResourcesUpdatesSignal = signal;
            this.vmInfo = {
              vm: of(null),
              resourcesUsed: merge(this.teamService.getVmsResourcesUsed(this.teamId), this.teamVmsResourcesUpdatesSignal.data()).pipe(
                scan((resources: TeamVmsResources, update: TeamVmsResources | TeamVmsResourcesSignal | null) => {
                  if(update == null)
                    return resources;
                  if(!(update instanceof TeamVmsResourcesSignal))
                    return update;
    
                  if(update.updateType == TeamVmsResourcesSignalUpdateType.USED)
                    resources = update.vmsResources;
                  return resources;
                }, null)
              ),
              resourcesLimits: merge(this.teamService.getVmsResourcesLimits(this.teamId), this.teamVmsResourcesUpdatesSignal.data()).pipe(
                scan((resources: TeamVmsResources, update: TeamVmsResources | TeamVmsResourcesSignal | null) => {
                  if(update == null)
                    return resources;
                  if(!(update instanceof TeamVmsResourcesSignal))
                    return update;
    
                  if(update.updateType == TeamVmsResourcesSignalUpdateType.TOTAL)
                    resources = update.vmsResources;
                  return resources;
                }, null)
              )
            };
          });
        }
      });

      this.course$.subscribe(course => {
        this.courseName = course.name;
        this.navigationData = [navHome, navCourses, nav(course.name, `/student/course/${course.code}`), nav('VMs', `/student/course/${course.code}/vms`), nav('New')];
      });
    });
  }
  ngOnDestroy(): void {
    this.teamVmsResourcesUpdatesSignal.unsubscribe();
  }

  saveVm(vmData) {
    this.formComponent.lock();
    const { vcpu, disk, ram } = vmData;
    this.vmService.add(vcpu, disk, ram, this.teamId).subscribe(res => {
      this.formComponent.unlock();
      if(res) {
        this.router.navigate([`/student/course/${this.courseCode}`]);
        this.toastService.show({ type: 'success', text: 'VM created successfully.' });
      } else
        this.toastService.show({ type: 'danger', text: 'An error occurred.' });
    });
  }
}

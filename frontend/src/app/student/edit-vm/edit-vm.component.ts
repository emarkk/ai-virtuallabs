import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, merge, forkJoin } from 'rxjs';
import { map, scan } from 'rxjs/operators';

import { APIResult } from 'src/app/core/models/api-result.model';
import { Course } from 'src/app/core/models/course.model';
import { VmModel } from 'src/app/core/models/vmmodel.model';
import { TeamStatus } from 'src/app/core/models/team.model';
import { Vm } from 'src/app/core/models/vm.model';
import { TeamVmsResources } from 'src/app/core/models/team-vms-resources.model';

import { VmSignal, VmSignalUpdateType } from 'src/app/core/models/signals/vm.signal';
import { TeamVmsResourcesSignal, TeamVmsResourcesSignalUpdateType } from 'src/app/core/models/signals/team-vms-resources.signal';

import { CourseService } from 'src/app/core/services/course.service';
import { VmService } from 'src/app/core/services/vm.service';
import { ToastService } from 'src/app/core/services/toast.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { StudentService } from 'src/app/core/services/student.service';
import { TeamService } from 'src/app/core/services/team.service';
import { SignalService, SignalObservable } from 'src/app/core/services/signal.service';

import { VmFormComponent } from 'src/app/components/vm-form/vm-form.component';

import { navHome, navCourses, nav } from '../student.navdata';

@Component({
  selector: 'app-student-edit-vm',
  templateUrl: './edit-vm.component.html',
  styleUrls: ['./edit-vm.component.css']
})
export class StudentEditVmComponent implements OnInit, OnDestroy {
  courseCode: string;
  courseName: string;
  course$: Observable<Course>;

  vmId: number;
  vmModel: VmModel;
  teamId: number;
  
  vmInfo: { vm: Observable<Vm>, resourcesUsed: Observable<TeamVmsResources>, resourcesLimits: Observable<TeamVmsResources> };
  vmUpdatesSignal: SignalObservable<VmSignal>;
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

      this.vmId = params.id;
      
      this.courseService.getVmModel(this.courseCode).subscribe(model => {
        this.vmModel = model;
      });
      
      this.studentService.getTeamsForCourse(this.authService.getId(), this.courseCode).pipe(map(teams => teams.find(t => t.status == TeamStatus.COMPLETE))).subscribe(team => {
        if(team) {
          this.teamId = team.id;

          forkJoin(
            this.signalService.teamVmsUpdates(this.teamId),
            this.signalService.teamVmsResourcesUpdates(this.teamId)
          ).subscribe(([vmSignal, vmsResourcesSignal]) => {
            this.vmUpdatesSignal = vmSignal;
            this.teamVmsResourcesUpdatesSignal = vmsResourcesSignal;
            this.vmInfo = {
              vm: merge(this.vmService.get(this.vmId), this.vmUpdatesSignal.data()).pipe(
                scan((vm: Vm, update: Vm | VmSignal | null) => {
                  if(update == null)
                    return vm;
                  if(!(update instanceof VmSignal))
                    return update;
    
                  if(update.updateType == VmSignalUpdateType.UPDATED)
                    vm = update.vm;
                  return vm;
                }, null)
              ),
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
        this.navigationData = [navHome, navCourses, nav(course.name, `/student/course/${course.code}`), nav('VMs', `/student/course/${course.code}/vms`), nav('Edit')];
      });
    });
  }
  ngOnDestroy(): void {
    if(this.vmUpdatesSignal)
      this.vmUpdatesSignal.unsubscribe();
    if(this.teamVmsResourcesUpdatesSignal)
      this.teamVmsResourcesUpdatesSignal.unsubscribe();
  }

  saveVm(vmData) {
    this.formComponent.lock();
    const { vcpu, disk, ram } = vmData;
    this.vmService.update(this.vmId, vcpu, disk, ram).subscribe((res: APIResult) => {
      this.formComponent.unlock();
      if(res.ok) {
        this.router.navigate([`/student/course/${this.courseCode}`]);
        this.toastService.show({ type: 'success', text: 'VM information updated successfully.' });
      } else
        this.toastService.show({ type: 'danger', text: res.errorMessage });
    });
  }
}

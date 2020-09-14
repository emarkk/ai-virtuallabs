import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';
import { Observable, combineLatest, merge } from 'rxjs';
import { scan } from 'rxjs/operators';

import { Course } from 'src/app/core/models/course.model';
import { Vm } from 'src/app/core/models/vm.model';
import { Team } from 'src/app/core/models/team.model';
import { TeamVmsResources } from 'src/app/core/models/team-vms-resources.model';

import { VmSignal, VmSignalUpdateType } from 'src/app/core/models/signals/vm.signal';
import { TeamVmsResourcesSignal, TeamVmsResourcesSignalUpdateType } from 'src/app/core/models/signals/team-vms-resources.signal';

import { CourseService } from 'src/app/core/services/course.service';
import { TeamService } from 'src/app/core/services/team.service';
import { SignalService, SignalObservable } from 'src/app/core/services/signal.service';
import { ToastService } from 'src/app/core/services/toast.service';

import { VmLimitsDialog } from 'src/app/components/dialogs/vm-limits/vm-limits.component';

import { navHome, navCourses, nav } from '../professor.navdata';

@Component({
  selector: 'app-professor-vms',
  templateUrl: './vms.component.html',
  styleUrls: ['./vms.component.css']
})
export class ProfessorVmsComponent implements OnInit, OnDestroy {
  courseCode: string;
  course$: Observable<Course>;
  
  teamsVms$: Observable<{ team: Team, vm: Vm }[]>;
  columnsToDisplay: string[] = ['team', 'vm', 'creator', 'online', '_connect'];

  vmUpdatesSignal: SignalObservable<VmSignal>;
  teamVmsResourcesUpdatesSignal: SignalObservable<TeamVmsResourcesSignal>;
  
  vmLimitsDialogRef: MatDialogRef<VmLimitsDialog> = null;

  navigationData: Array<any>|null = null;

  constructor(private route: ActivatedRoute, private router: Router, private courseService: CourseService, private teamService: TeamService, private signalService: SignalService, private dialog: MatDialog, private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.courseCode = this.route.snapshot.params.code;
    this.init();
  }
  ngOnDestroy(): void {
    if(this.vmUpdatesSignal)
      this.vmUpdatesSignal.unsubscribe();
  }
  init(): void {
    this.course$ = this.courseService.get(this.courseCode);
    this.course$.subscribe(course => {
      this.navigationData = [navHome, navCourses, nav(course.name, `/professor/course/${course.code}`), nav('VMs')];
    });
    
    this.signalService.courseVmsUpdates(this.courseCode).subscribe(signal => {
      this.vmUpdatesSignal = signal;
      this.teamsVms$ = merge(this.courseService.getTeamsAndVms(this.courseCode), this.vmUpdatesSignal.data()).pipe(
        scan((teamsVms, update) => {
          if(!(update instanceof VmSignal))
            return update;

          if(update.updateType == VmSignalUpdateType.CREATED) {
            teamsVms = teamsVms.some(tv => tv.team.id == update.teamId && tv.vm == null)
              ? teamsVms.map(tv => tv.team.id == update.teamId && tv.vm == null ? { team: tv.team, vm: update.vm } : tv)
              : teamsVms.concat({ team: teamsVms.find(tv => tv.team.id == update.teamId).team, vm: update.vm }).sort((a, b) => a.team.id - b.team.id || a.vm.id - b.vm.id);
          } else if(update.updateType == VmSignalUpdateType.UPDATED)
            teamsVms = teamsVms.map(tv => tv.vm.id == update.vm.id ? { team: tv.team, vm: update.vm } : tv);
          else if(update.updateType == VmSignalUpdateType.DELETED)
            teamsVms = teamsVms.filter(tv => tv.team.id == update.teamId).length > 1
              ? teamsVms.filter(tv => tv.vm.id != update.vm.id)
              : teamsVms.map(tv => tv.team.id == update.teamId ? { team: tv.team, vm: null } : tv);
          return teamsVms;
        }, [])
      );

      combineLatest([this.route.queryParams, this.teamsVms$]).subscribe(([queryParams, teamsVms]) => {
        if(teamsVms && queryParams.edit == 'vm-limits') {
          let resourcesUsed: Observable<TeamVmsResources>;
          const team = teamsVms.find(tv => tv.team.id == queryParams.team).team;
          if(!team)
            this.router.navigate([]);
  
          if(!this.vmLimitsDialogRef) {
            this.signalService.teamVmsResourcesUpdates(team.id).subscribe(signal => {
              this.teamVmsResourcesUpdatesSignal = signal;
              resourcesUsed = merge(this.teamService.getVmsResourcesUsed(team.id), this.teamVmsResourcesUpdatesSignal.data()).pipe(
                scan((resources: TeamVmsResources, update: TeamVmsResources | TeamVmsResourcesSignal | null) => {
                  if(update == null)
                    return resources;
                  if(!(update instanceof TeamVmsResourcesSignal))
                    return update;
    
                  if(update.updateType == TeamVmsResourcesSignalUpdateType.USED)
                    resources = update.vmsResources;
                  return resources;
                }, null)
              );
    
              this.vmLimitsDialogRef = this.dialog.open(VmLimitsDialog, {
                data: {
                  teamId: team.id,
                  teamName: team.name,
                  resourcesUsed$: resourcesUsed,
                  resourcesLimits$: this.teamService.getVmsResourcesLimits(team.id)
                }
              });
            });
          }
  
          this.vmLimitsDialogRef.afterClosed().subscribe(res => {
            if(res)
              this.toastService.show({ type: 'success', text: 'VMs resource limits saved successfully.' });
            else if(res === false)
              this.toastService.show({ type: 'danger', text: 'An error occurred.' });

            this.router.navigate([]);
            this.vmLimitsDialogRef = null;
            if(this.teamVmsResourcesUpdatesSignal)
              this.teamVmsResourcesUpdatesSignal.unsubscribe();
          });
        } else if(this.vmLimitsDialogRef) {
          this.vmLimitsDialogRef.close();
          this.vmLimitsDialogRef = null;
        }
      });
    });
  }
  
  getRowSpan(i: number, teamsVms: { team: Team, vm: Vm }[]): number {
    const currentTeamId = teamsVms[i].team.id;
    return teamsVms.filter(x => x.team.id == currentTeamId).length;
  }
  display(i: number, teamsVms: { team: Team, vm: Vm }[]): boolean {
    return i == 0 || teamsVms[i].team.id != teamsVms[i-1].team.id;
  }
}

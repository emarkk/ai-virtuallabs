import { Component, OnInit, Input } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Router, ActivatedRoute } from '@angular/router';
import { Observable, BehaviorSubject, combineLatest, of, merge, forkJoin } from 'rxjs';
import { switchMap, scan } from 'rxjs/operators';

import { APIResult } from 'src/app/core/models/api-result.model';
import { Vm } from 'src/app/core/models/vm.model';
import { Team } from 'src/app/core/models/team.model';
import { TeamVmsResources } from 'src/app/core/models/team-vms-resources.model';

import { VmSignal, VmSignalUpdateType } from 'src/app/core/models/signals/vm.signal';
import { TeamVmsResourcesSignal, TeamVmsResourcesSignalUpdateType } from 'src/app/core/models/signals/team-vms-resources.signal';

import { TeamService } from 'src/app/core/services/team.service';
import { VmService } from 'src/app/core/services/vm.service';
import { ToastService } from 'src/app/core/services/toast.service';
import { SignalService, SignalObservable } from 'src/app/core/services/signal.service';

import { ConfirmDialog } from 'src/app/components/dialogs/confirm/confirm.component';
import { VmAddOwnersDialog } from 'src/app/components/dialogs/vm-add-owners/vm-add-owners.component';

@Component({
  selector: 'app-student-vms-detail',
  templateUrl: './vms-detail.component.html',
  styleUrls: ['./vms-detail.component.css']
})
export class StudentVmsDetailComponent implements OnInit {
  courseCode: string;
  joinedTeam: Team;

  vms$: Observable<Vm[]>;
  vmsRefreshToken = new BehaviorSubject(undefined);
  vmsResourcesLimits$: Observable<TeamVmsResources>;

  vmUpdatesSignal: SignalObservable<VmSignal>;
  vmsResourcesUpdatesSignal: SignalObservable<TeamVmsResourcesSignal>;

  vmAddOwnersDialogRef: MatDialogRef<VmAddOwnersDialog> = null;
  
  @Input() set course(value: string) {
    this.courseCode = value;
  }
  @Input() set team(value: Team) {
    this.joinedTeam = value;

    if(this.joinedTeam) {
      forkJoin([
        this.signalService.teamVmsUpdates(this.joinedTeam.id),
        this.signalService.teamVmsResourcesUpdates(this.joinedTeam.id)
      ]).subscribe(([vmSignal, vmsLimitsSignal]) => {
        this.vmUpdatesSignal = vmSignal;
        this.vmsResourcesUpdatesSignal = vmsLimitsSignal;
        this.vmsRefreshToken.next(undefined);
      });
    }
  }
  
  constructor(private router: Router, private route: ActivatedRoute, private teamService: TeamService,
      private vmService: VmService, private signalService: SignalService, private dialog: MatDialog, private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.vms$ = this.vmsRefreshToken.pipe(
      switchMap(() => merge(
        this.joinedTeam ? this.teamService.getVms(this.joinedTeam.id) : of(null),
        this.vmUpdatesSignal ? this.vmUpdatesSignal.data() : of(null)
      )),
      scan((vms: Vm[], update: Vm[] | VmSignal | null) => {
        if(update == null)
          return vms;
        if(!(update instanceof VmSignal))
          return update;
          
        if(update.updateType == VmSignalUpdateType.CREATED)
          vms = vms.concat(update.vm);
        else if(update.updateType == VmSignalUpdateType.UPDATED)
          vms = vms.map(vm => vm.id == update.vm.id ? update.vm : vm);
        else if(update.updateType == VmSignalUpdateType.DELETED)
          vms = vms.filter(vm => vm.id != update.vm.id);
        return vms;
      }, [])
    );
    
    this.vmsResourcesLimits$ = this.vmsRefreshToken.pipe(
      switchMap(() => merge(
        this.joinedTeam ? this.teamService.getVmsResourcesLimits(this.joinedTeam.id) : of(null),
        this.vmsResourcesUpdatesSignal ? this.vmsResourcesUpdatesSignal.data() : of(null)
      )),
      scan((limits: TeamVmsResources, update: TeamVmsResources | TeamVmsResourcesSignal | null) => {
        if(update == null)
          return limits;
        if(!(update instanceof TeamVmsResourcesSignal))
          return update;

        if(update.updateType == TeamVmsResourcesSignalUpdateType.TOTAL)
          limits = update.vmsResources;
        return limits;
      }, null)
    );

    combineLatest([this.route.queryParams, this.vms$]).subscribe(([queryParams, vms]) => {
      if(vms && queryParams.edit == 'vm-owners') {
        const vm = vms.find(vm => vm.id == queryParams.vm);
        if(!vm || vm.online) {
          this.router.navigate([]);
          return;
        }

        this.vmAddOwnersDialogRef = this.dialog.open(VmAddOwnersDialog, {
          data: {
            vmId: vm.id,
            students: this.joinedTeam.members.map(m => m.student),
            ownersIds: vm.ownersIds
          }
        });
        this.vmAddOwnersDialogRef.afterClosed().subscribe(res => {
          if(res instanceof APIResult) {
            if(res.ok)
              this.toastService.show({ type: 'success', text: 'VM owners updated successfully.' });
            else if(res.error)
              this.toastService.show({ type: 'danger', text: res.errorMessage });
          }
          
          this.router.navigate([]);
        });
      } else if(this.vmAddOwnersDialogRef)
        this.vmAddOwnersDialogRef.close();
    });
  }
  ngOnDestroy(): void {
    if(this.vmUpdatesSignal)
      this.vmUpdatesSignal.unsubscribe();
    if(this.vmsResourcesUpdatesSignal)
      this.vmsResourcesUpdatesSignal.unsubscribe();
  }

  changeVmState(data: { vmId: number, online: boolean }) {
    this.vmService.turnOnOff(data.vmId, data.online).subscribe((res: APIResult) => {
      if(res.ok)
        this.vmsRefreshToken.next(undefined);
    });
  }
  connectToVm(vmId: number) {
    this.router.navigate([`/student/course/${this.courseCode}/vm/${vmId}`]);
  }
  editVm(vmId: number) {
    this.router.navigate([`/student/course/${this.courseCode}/vm/${vmId}/edit`]);
  }
  addVmOwners(vmId: number) {
    this.router.navigate([], { queryParams: { edit: 'vm-owners', vm: vmId } });
  }
  deleteVm(vmId: number) {
    this.dialog.open(ConfirmDialog, {
      data: {
        title: 'Delete VM',
        message: 'Are you sure you want to delete this VM?'
      }
    }).afterClosed().subscribe(confirmed => {
      if(confirmed) {
        this.vmService.delete(vmId).subscribe((res: APIResult) => {
          if(res.ok) {
            this.vmsRefreshToken.next(undefined);
            this.toastService.show({ type: 'success', text: 'VM deleted successfully.' });
          } else
            this.toastService.show({ type: 'danger', text: res.errorMessage });
        });
      }
    });
  }
}

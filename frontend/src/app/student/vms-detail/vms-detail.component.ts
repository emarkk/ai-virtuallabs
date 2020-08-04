import { Component, OnInit, Input } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Router, ActivatedRoute } from '@angular/router';
import { Observable, BehaviorSubject, combineLatest, of, merge } from 'rxjs';
import { switchMap, scan } from 'rxjs/operators';

import { Vm } from 'src/app/core/models/vm.model';
import { Team } from 'src/app/core/models/team.model';
import { TeamVmsResources } from 'src/app/core/models/team-vms-resources.model';

import { VmSignal, VmSignalUpdateType } from 'src/app/core/models/signals/vm.signal';

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

  updatesSignal: SignalObservable<VmSignal>;

  vmAddOwnersDialogRef: MatDialogRef<VmAddOwnersDialog> = null;
  
  @Input() set course(value: string) {
    this.courseCode = value;
  }
  @Input() set team(value: Team) {
    this.joinedTeam = value;

    if(this.joinedTeam) {
      this.signalService.teamVmsUpdates(this.joinedTeam.id).subscribe(signal => {
        this.updatesSignal = signal;
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
        this.updatesSignal ? this.updatesSignal.data() : of(null)
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
      switchMap(() => this.joinedTeam ? this.teamService.getVmsResourceLimits(this.joinedTeam.id) : of(null))
    );

    combineLatest(this.route.queryParams, this.vms$).subscribe(([queryParams, vms]) => {
      if(vms && queryParams.edit == 'vm-owners') {
        const vm = vms.find(vm => vm.id == queryParams.vm);
        if(!vm || vm.online)
          this.router.navigate([]);

        this.vmAddOwnersDialogRef = this.dialog.open(VmAddOwnersDialog, {
          data: {
            vmId: vm.id,
            students: this.joinedTeam.members.map(m => m.student),
            ownersIds: vm.ownersIds
          }
        });
        this.vmAddOwnersDialogRef.afterClosed().subscribe(res => {
          if(res)
            this.toastService.show({ type: 'success', text: 'VM owners updated successfully.' });
          else if(res === false)
            this.toastService.show({ type: 'danger', text: 'An error occurred.' });
            
          this.router.navigate([]);
        });
      } else if(this.vmAddOwnersDialogRef)
        this.vmAddOwnersDialogRef.close();
    });
  }
  ngOnDestroy(): void {
    this.updatesSignal.unsubscribe();
  }

  changeVmState(data: { vmId: number, online: boolean }) {
    this.vmService.turnOnOff(data.vmId, data.online).subscribe(res => {
      if(res) {
        this.vmsRefreshToken.next(undefined);
      }
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
        this.vmService.delete(vmId).subscribe(res => {
          if(res) {
            this.vmsRefreshToken.next(undefined);
            this.toastService.show({ type: 'success', text: 'VM deleted successfully.' });
          } else
            this.toastService.show({ type: 'danger', text: 'An error occurred.' });
        });
      }
    });
  }
}

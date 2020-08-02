import { Component, OnInit, Input } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Router, ActivatedRoute } from '@angular/router';
import { Observable, BehaviorSubject, combineLatest, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';

import { Vm } from 'src/app/core/models/vm.model';
import { Team } from 'src/app/core/models/team.model';

import { TeamService } from 'src/app/core/services/team.service';
import { VmService } from 'src/app/core/services/vm.service';
import { ToastService } from 'src/app/core/services/toast.service';

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
  vmAddOwnersDialogRef: MatDialogRef<VmAddOwnersDialog> = null;
  
  @Input() set course(value: string) {
    this.courseCode = value;
  }
  @Input() set team(value: Team) {
    this.joinedTeam = value;
    this.vmsRefreshToken.next(undefined);
  }
  
  constructor(private router: Router, private route: ActivatedRoute, private teamService: TeamService,
      private vmService: VmService, private dialog: MatDialog, private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.vms$ = this.vmsRefreshToken.pipe(
      switchMap(() => this.joinedTeam ? this.teamService.getVms(this.joinedTeam.id) : of(null))
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
          if(res) {
            this.vmsRefreshToken.next(undefined);
            this.toastService.show({ type: 'success', text: 'VM owners updated successfully.' });
          } else if(res === false)
            this.toastService.show({ type: 'danger', text: 'An error occurred.' });
            
          this.router.navigate([]);
        });
      } else if(this.vmAddOwnersDialogRef)
        this.vmAddOwnersDialogRef.close();
    });
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
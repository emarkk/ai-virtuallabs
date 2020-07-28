import { Component, OnInit, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable, BehaviorSubject } from 'rxjs';
import { switchMap } from 'rxjs/operators';

import { Vm } from 'src/app/core/models/vm.model';

import { TeamService } from 'src/app/core/services/team.service';
import { VmService } from 'src/app/core/services/vm.service';
import { ToastService } from 'src/app/core/services/toast.service';

import { ConfirmDialog } from 'src/app/components/dialogs/confirm/confirm.component';

@Component({
  selector: 'app-student-course-vms-detail',
  templateUrl: './vms-detail.component.html',
  styleUrls: ['./vms-detail.component.css']
})
export class StudentCourseVmsDetailComponent implements OnInit {

  courseCode: string;
  teamId: number;

  vms$: Observable<Vm[]>;
  vmsRefreshToken = new BehaviorSubject(undefined);
  
  @Input() set course(value: string) {
    this.courseCode = value;
  }
  @Input() set team(value: number) {
    this.teamId = value;
    this.vms$ = this.vmsRefreshToken.pipe(
      switchMap(() => this.teamService.getVms(this.teamId))
    );
  }
  
  constructor(private teamService: TeamService, private vmService: VmService, private dialog: MatDialog, private toastService: ToastService) {
  }

  ngOnInit(): void {
  }

  addVmOwners(vmId: number) {
    //
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

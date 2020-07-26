import { Component, OnInit, Input } from '@angular/core';
import { Observable } from 'rxjs';

import { Vm } from 'src/app/core/models/vm.model';

import { TeamService } from 'src/app/core/services/team.service';

@Component({
  selector: 'app-student-course-vms-detail',
  templateUrl: './vms-detail.component.html',
  styleUrls: ['./vms-detail.component.css']
})
export class StudentCourseVmsDetailComponent implements OnInit {

  courseCode: string;
  teamId: number;

  vms$: Observable<Vm[]>;
  
  @Input() set course(value: string) {
    this.courseCode = value;
  }
  @Input() set team(value: number) {
    this.teamId = value;
    this.vms$ = this.teamService.getVms(this.teamId);
  }
  
  constructor(private teamService: TeamService) {
  }

  ngOnInit(): void {
  }

}

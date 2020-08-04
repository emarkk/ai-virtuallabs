import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { Student } from '../models/student.model';
import { Team } from '../models/team.model';
import { Vm } from '../models/vm.model';
import { TeamVmsResources } from '../models/team-vms-resources.model';

import { url, httpOptions } from '../utils';

@Injectable({
  providedIn: 'root'
})
export class TeamService {
  
  constructor(private http: HttpClient) {
  }

  // get vms of team
  getVms(teamId: number): Observable<Vm[]> {
    return this.http.get<any[]>(url(`teams/${teamId}/vms`)).pipe(
      map(arr => arr.map(x => new Vm(x.id, x.vcpus, x.diskSpace, x.ram, x.online, x.owners, new Student(x.creator.id, x.creator.firstName, x.creator.lastName, x.creator.email, x.creator.hasPicture)))),
      catchError(error => of(null))
    );
  }
  // get vms resource limits for team
  getVmsResourceLimits(teamId: number): Observable<TeamVmsResources> {
    return this.http.get<any>(url(`teams/${teamId}/vms/limits`)).pipe(
      map(x => x ? new TeamVmsResources(x.vCpus, x.diskSpace, x.ram, x.instances, x.activeInstances) : Team.DEFAULT_VMS_RESOURCES_LIMITS),
      catchError(error => of(null))
    );
  }
  // propose a new team
  propose(name: string, timeout: number, membersIds: number[], courseCode: string): Observable<boolean> {
    return this.http.post(url('teams'), { name, timeout, membersIds, courseCode }, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  // accept an invitation to a team
  acceptTeamInvitation(teamId: number): Observable<boolean> {
    return this.http.post(url(`teams/${teamId}/accept`), null, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  // decline an invitation to a team
  declineTeamInvitation(teamId: number): Observable<boolean> {
    return this.http.post(url(`teams/${teamId}/decline`), null, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
}
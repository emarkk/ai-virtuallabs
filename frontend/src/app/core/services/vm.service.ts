import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { APIResult } from '../models/api-result.model';
import { VmModel } from '../models/vmmodel.model';
import { Vm } from '../models/vm.model';
import { Student } from '../models/student.model';

import { url, httpOptions } from '../utils';

@Injectable({
  providedIn: 'root'
})
export class VmService {
  
  constructor(private http: HttpClient) {
  }

  // get vm model
  getModel(id: number): Observable<VmModel> {
    if(id == null)
      return of(new VmModel(null, null, null));

    return this.http.get<VmModel>(url(`vms/models/${id}`)).pipe(
      catchError(error => of(null))
    );
  }
  // add vm model for course
  addModel(name: string, configuration: string, courseCode: string): Observable<APIResult> {
    return this.http.post(url('vms/models'), { name, configuration, courseCode }, httpOptions).pipe(
      map(res => APIResult.ok(res)),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
  // update vm model
  updateModel(id: number, name: string, configuration: string): Observable<APIResult> {
    return this.http.put(url(`vms/models/${id}`), { id, name, configuration }, httpOptions).pipe(
      map(res => APIResult.ok(res)),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
  // get vm
  get(id: number): Observable<Vm> {
    return this.http.get<any>(url(`vms/${id}`)).pipe(
      map(x => new Vm(x.id, x.vcpus, x.diskSpace, x.ram, x.online, x.owners, new Student(x.creator.id, x.creator.firstName, x.creator.lastName, x.creator.email, x.creator.hasPicture))),
      catchError(error => of(null))
    );
  }
  // add vm
  add(vcpus: number, diskSpace: number, ram: number, teamId: number): Observable<APIResult> {
    return this.http.post(url('vms'), { vcpus, diskSpace, ram, teamId }, httpOptions).pipe(
      map(res => APIResult.ok(res)),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
  // update vm
  update(id: number, vcpus: number, diskSpace: number, ram: number): Observable<APIResult> {
    return this.http.put(url(`vms/${id}`), { vcpus, diskSpace, ram }, httpOptions).pipe(
      map(res => APIResult.ok(res)),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
  // turn on / turn off vm
  turnOnOff(id: number, online: boolean): Observable<APIResult> {
    const action = online ? 'on' : 'off';
    return this.http.post(url(`vms/${id}/${action}`), null, httpOptions).pipe(
      map(res => APIResult.ok(res)),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
  // add owners to vm
  addOwners(id: number, studentIds: number[]): Observable<APIResult> {
    return this.http.patch(url(`vms/${id}/owners`), studentIds, httpOptions).pipe(
      map(res => APIResult.ok(res)),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
  // delete vm
  delete(id: number): Observable<APIResult> {
    return this.http.delete(url(`vms/${id}`)).pipe(
      map(res => APIResult.ok(res)),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
}
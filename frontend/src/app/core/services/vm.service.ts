import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { VmModel } from '../models/vmmodel.model';
import { Vm } from '../models/vm.model';

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
  addModel(name: string, configuration: string, courseCode: string): Observable<boolean> {
    return this.http.post(url('vms/models'), { name, configuration, courseCode }, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  // update vm model
  updateModel(id: number, name: string, configuration: string): Observable<boolean> {
    return this.http.put(url(`vms/models/${id}`), { id, name, configuration }, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  // get vm
  get(id: number): Observable<Vm> {
    return this.http.get<any>(url(`vms/${id}`)).pipe(
      map(x => new Vm(x.id, x.vcpus, x.diskSpace, x.ram, x.online, x.owners)),
      catchError(error => of(null))
    );
  }
  // add vm
  add(vCpus: number, diskSpace: number, ram: number, teamId: number): Observable<boolean> {
    return this.http.post(url('vms'), { vCpus, diskSpace, ram, teamId }, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  // update vm
  update(id: number, vCpus: number, diskSpace: number, ram: number): Observable<boolean> {
    return this.http.put(url(`vms/${id}`), { vCpus, diskSpace, ram }, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  // turn on / turn off vm
  turnOnOff(id: number, online: boolean): Observable<boolean> {
    const action = online ? 'on' : 'off';
    return this.http.post(url(`vms/${id}/${action}`), null, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  // add owners to vm
  addOwners(id: number, studentIds: number[]): Observable<boolean> {
    return this.http.patch(url(`vms/${id}/owners`), studentIds, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  // delete vm
  delete(id: number): Observable<boolean> {
    return this.http.delete(url(`vms/${id}`)).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
}
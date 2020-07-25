import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { url, httpOptions } from '../utils';
import { VmModel } from '../models/vmmodel.model';

@Injectable({
  providedIn: 'root'
})
export class VmService {
  
  constructor(private http: HttpClient) {
  }

  // get vm model
  get(id: number): Observable<VmModel> {
    return this.http.get<VmModel>(url(`vms/models/${id}`)).pipe(
      catchError(error => of(null))
    );
  }
  // add vm model for course
  add(name: string, configuration: string, courseCode: string): Observable<boolean> {
    return this.http.post(url('vms/models'), { name, configuration, courseCode }, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  // update vm model
  update(id: number, name: string, configuration: string): Observable<boolean> {
    return this.http.put(url(`vms/models/${id}`), { id, name, configuration }, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
}
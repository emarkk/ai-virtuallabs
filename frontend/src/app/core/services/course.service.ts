import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { Course } from '../models/course.model';

import { url, httpOptions } from '../utils';

@Injectable({
  providedIn: 'root'
})
export class CourseService {
  private _insertionSuccessful: boolean = false;
  
  constructor(private http: HttpClient) {
  }

  public hasInsertedSuccessfully(): boolean {
    const value = this._insertionSuccessful;
    this._insertionSuccessful = false;
    return value;
  }

  public insertionSuccessful() {
    this._insertionSuccessful = true;
  }

  get(code: string): Observable<Course> {
    return this.http.get<Course>(url('courses/' + code)).pipe(
      map(x => new Course(x.code, x.name, x.acronym, x.minTeamMembers, x.maxTeamMembers, x.enabled)),
      catchError(error => of(null))
    );
  }
  add(code: string, name: string, acronym: string, minTeamMembers: number, maxTeamMembers: number, enabled: boolean): Observable<boolean> {
    return this.http.post<boolean>(url('courses'), { code, name, acronym, minTeamMembers, maxTeamMembers, enabled }, httpOptions).pipe(
      catchError(error => of(null))
    );
  }
}
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
  
  constructor(private http: HttpClient) {
  }

  add(code: string, name: string, acronym: string, minTeamMembers: number, maxTeamMembers: number, enabled: boolean): Observable<boolean> {
    return this.http.post<boolean>(url('courses'), { code, name, acronym, minTeamMembers, maxTeamMembers, enabled }, httpOptions).pipe(
      catchError(error => of(null))
    );
  }
}
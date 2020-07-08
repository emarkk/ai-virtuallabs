import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { Course } from '../models/course.model';

import { url } from '../utils';

@Injectable({
  providedIn: 'root'
})
export class ProfessorService {
  
  constructor(private http: HttpClient) {
  }

  getCourses(professorId: number): Observable<Course[]> {
    return this.http.get<Course[]>(url('professors/' + professorId + '/courses')).pipe(
      map(arr => arr.map(x => new Course(x.code, x.name, x.acronym, x.minTeamMembers, x.maxTeamMembers, x.enabled, '/professor/course/' + x.code))),
      catchError(error => of(null))
    );
  }
}
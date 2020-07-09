import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { Course } from '../models/course.model';
import { Professor } from '../models/professor.model';

import { url } from '../utils';

@Injectable({
  providedIn: 'root'
})
export class ProfessorService {
  
  constructor(private http: HttpClient) {
  }

  get(professorId: number): Observable<Professor> {
    return this.http.get<Professor>(url('professors/' + professorId)).pipe(
      map(x => new Professor(x.id, x.firstName, x.lastName, x.email, x.hasPicture)),
      catchError(error => of(null))
    );
  }
  search(query: string): Observable<Professor[]> {
    return this.http.get<Professor[]>(url('professors/search?q=' + query)).pipe(
      map(arr => arr.map(x => new Professor(x.id, x.firstName, x.lastName, x.email, x.hasPicture))),
      catchError(error => of(null))
    );
  }
  getCourses(professorId: number): Observable<Course[]> {
    return this.http.get<Course[]>(url('professors/' + professorId + '/courses')).pipe(
      map(arr => arr.map(x => new Course(x.code, x.name, x.acronym, x.minTeamMembers, x.maxTeamMembers, x.enabled, '/professor/course/' + x.code))),
      catchError(error => of(null))
    );
  }
}
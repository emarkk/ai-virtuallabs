import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { Course } from '../models/course.model';
import { Professor } from '../models/professor.model';

import { url } from '../utils';

export class ProfessorSearchFilters {
  excludeCourse: string;

  constructor(filters: any) {
    this.excludeCourse = filters.excludeCourse;
  }
  toParams(params: HttpParams): HttpParams {
    return Object.getOwnPropertyNames(this).reduce((p, i) => this[i] !== undefined ? p.set(i, this[i]) : p, params);
  }
}

@Injectable({
  providedIn: 'root'
})
export class ProfessorService {
  
  constructor(private http: HttpClient) {
  }

  // get professor by id
  get(professorId: number): Observable<Professor> {
    return this.http.get<any>(url(`professors/${professorId}`)).pipe(
      map(x => new Professor(x.id, x.firstName, x.lastName, x.email, x.hasPicture)),
      catchError(error => of(null))
    );
  }
  // search professor by query and filters
  search(query: string, filters: ProfessorSearchFilters): Observable<Professor[]> {
    let params = filters.toParams(new HttpParams().set('q', query));
    return this.http.get<any[]>(url('professors/search'), { params }).pipe(
      map(arr => arr.map(x => new Professor(x.id, x.firstName, x.lastName, x.email, x.hasPicture))),
      catchError(error => of(null))
    );
  }
  // get professor courses
  getCourses(professorId: number): Observable<Course[]> {
    return this.http.get<Course[]>(url(`professors/${professorId}/courses`)).pipe(
      map(arr => arr.map(x => new Course(x.code, x.name, x.acronym, x.minTeamMembers, x.maxTeamMembers, x.enabled, `/professor/course/${x.code}`))),
      catchError(error => of(null))
    );
  }
  setProfilePicture(professorId: number, file: File): Observable<boolean> {
    const formData: FormData = new FormData();
    formData.append('file', file);
    return this.http.post(url(`professors/${professorId}/picture`), formData).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
}
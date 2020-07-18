import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { Course } from '../models/course.model';
import { Student } from '../models/student.model';

import { url } from '../utils';

export class StudentSearchFilters {
  course: string;
  excludeCourse: string;
  excludeIds: string;
  teamed: boolean;

  constructor(filters: any) {
    this.course = filters.course;
    this.excludeCourse = filters.excludeCourse;
    this.excludeIds = filters.excludeIds;
    this.teamed = filters.teamed;
  }
  toParams(params: HttpParams): HttpParams {
    return Object.getOwnPropertyNames(this).reduce((p, i) => this[i] !== undefined ? p.set(i, this[i]) : p, params);
  }
}

@Injectable({
  providedIn: 'root'
})
export class StudentService {
  
  constructor(private http: HttpClient) {
  }
  
  // get student by id
  get(studentId: number): Observable<Student> {
    return this.http.get<any>(url(`students/${studentId}`)).pipe(
      map(x => new Student(x.id, x.firstName, x.lastName, x.email, x.hasPicture)),
      catchError(error => of(null))
    );
  }
  // search student by query and filters
  search(query: string, filters: StudentSearchFilters): Observable<Student[]> {
    let params = filters.toParams(new HttpParams().set('q', query));
    return this.http.get<any[]>(url('students/search'), { params }).pipe(
      map(arr => arr.map(x => new Student(x.id, x.firstName, x.lastName, x.email, x.hasPicture))),
      catchError(error => of(null))
    );
  }
  // get student courses
  getCourses(studentId: number): Observable<Course[]> {
    return this.http.get<Course[]>(url(`students/${studentId}/courses`)).pipe(
      map(arr => arr.map(x => new Course(x.code, x.name, x.acronym, x.minTeamMembers, x.maxTeamMembers, x.enabled, `/student/course/${x.code}`))),
      catchError(error => of(null))
    );
  }
}
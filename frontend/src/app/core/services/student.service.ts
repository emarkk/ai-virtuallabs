import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { Course } from '../models/course.model';
import { Student } from '../models/student.model';

import { url } from '../utils';

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
  // search student by query (if excludeCourse is provided, do not select students enrolled in that course)
  search(query: string, excludeCourse?: string): Observable<Student[]> {
    let params = new HttpParams().set('q', query);
    if(excludeCourse)
      params = params.set('excludeCourse', excludeCourse);
      
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
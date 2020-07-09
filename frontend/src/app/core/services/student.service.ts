import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
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
  
  get(studentId: number): Observable<Student> {
    return this.http.get<Student>(url('students/' + studentId)).pipe(
      map(x => new Student(x.id, x.firstName, x.lastName, x.email, x.picturePath)),
      catchError(error => of(null))
    );
  }
  getCourses(studentId: number): Observable<Course[]> {
    return this.http.get<Course[]>(url('students/' + studentId + '/courses')).pipe(
      map(arr => arr.map(x => new Course(x.code, x.name, x.acronym, x.minTeamMembers, x.maxTeamMembers, x.enabled, '/student/course/' + x.code))),
      catchError(error => of(null))
    );
  }
}
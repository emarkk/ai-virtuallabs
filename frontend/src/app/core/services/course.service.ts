import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { Page } from '../models/page.model';
import { Course } from '../models/course.model';
import { Student } from '../models/student.model';
import { EnrolledStudent } from '../models/enrolledstudent.model';
import { Team, TeamStatus } from '../models/team.model';
import { Professor } from '../models/professor.model';
import { VmModel } from '../models/vmmodel.model';

import { url, httpOptions } from '../utils';

@Injectable({
  providedIn: 'root'
})
export class CourseService {
  
  constructor(private http: HttpClient) {
  }

  // get course by code
  get(code: string): Observable<Course> {
    return this.http.get<Course>(url(`courses/${code}`)).pipe(
      catchError(error => of(null))
    );
  }
  // get course students
  getStudents(code: string, sortBy: string = null, sortDirection: string = null, pageIndex: number = 0, pageSize: number = 15): Observable<Page<EnrolledStudent>> {
    let params = new HttpParams().set('page', pageIndex.toString()).set('pageSize', pageSize.toString());
    if(sortBy)
      params = params.set('sortBy', sortBy);
    if(sortDirection)
      params = params.set('sortDirection', sortDirection);

    return this.http.get<any>(url(`courses/${code}/enrolled`), { params }).pipe(
      map(x => new Page(x.total, x.page.map(e =>
        new EnrolledStudent(e.student.id, e.student.firstName, e.student.lastName, e.student.email, e.student.hasPicture,
          e.team ? new Team(e.team.id, e.team.name, e.team.status as TeamStatus, null, new Date(e.team.invitationExpiration), new Date(e.team.lastAction)) : null)
      ))),
      catchError(error => of(null))
    );
  }
  // get course professors
  getProfessors(code: string): Observable<Professor[]> {
    return this.http.get<any[]>(url(`courses/${code}/professors`)).pipe(
      map(arr => arr.map(x => new Professor(x.id, x.firstName, x.lastName, x.email, x.hasPicture))),
      catchError(error => of(null))
    );
  }
  // get course vm model
  getVmModel(code: string): Observable<VmModel> {
    return this.http.get<any>(url(`courses/${code}/vm/model`)).pipe(
      map(x => x ? new VmModel(x.id, x.name, null) : new VmModel(null, null, null)),
      catchError(error => of(null))
    );
  }
  // create new course
  add(code: string, name: string, acronym: string, minTeamMembers: number, maxTeamMembers: number, enabled: boolean): Observable<boolean> {
    return this.http.post(url('courses'), { code, name, acronym, minTeamMembers, maxTeamMembers, enabled }, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  // add professor to course
  addProfessor(code: string, professorId: number): Observable<boolean> {
    return this.http.post(url(`courses/${code}/professors`), { id: professorId }, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  // delete course
  update(code: string, name: string, acronym: string, minTeamMembers: number, maxTeamMembers: number, enabled: boolean): Observable<boolean> {
    return this.http.put(url(`courses/${code}`), { code, name, acronym, minTeamMembers, maxTeamMembers, enabled }, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  // delete course
  delete(code: string): Observable<boolean> {
    return this.http.delete(url(`courses/${code}`)).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  // enable/disable course
  setEnabled(code: string, enabled: boolean): Observable<boolean> {
    const action = enabled ? 'enable' : 'disable';
    return this.http.post(url(`courses/${code}/${action}`), null, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  // enroll student to course
  enroll(code: string, studentId: number): Observable<boolean> {
    return this.http.post(url(`courses/${code}/enroll`), { id: studentId }, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  // enroll students from CSV
  enrollFromCSV(code: string, csvFile: File): Observable<boolean> {
    const formData: FormData = new FormData();
    formData.append('csvFile', csvFile);
    return this.http.post(url(`courses/${code}/enroll/csv`), formData).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  // unenroll subset of students from course
  unenroll(code: string, studentIds: number[]): Observable<boolean> {
    return this.http.post(url(`courses/${code}/unenroll`), studentIds, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  // unenroll all students from course
  unenrollAll(code: string): Observable<boolean> {
    return this.http.post(url(`courses/${code}/unenroll/all`), null, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
}
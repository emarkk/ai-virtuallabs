import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { Course } from '../models/course.model';
import { Student } from '../models/student.model';
import { Professor } from '../models/professor.model';
import { Team } from '../models/team.model';

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
      map(x => new Course(x.code, x.name, x.acronym, x.minTeamMembers, x.maxTeamMembers, x.enabled)),
      catchError(error => of(null))
    );
  }
  // get course students
  getStudents(code: string): Observable<Student[]> {
    return this.http.get<any[]>(url(`courses/${code}/enrolled`)).pipe(
      map(arr => arr.map(x => new Professor(x.id, x.firstName, x.lastName, x.email, x.hasPicture))),
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
  // get course teams
  getTeams(code: string): Observable<Team[]> {
    return this.http.get<Team[]>(url(`courses/${code}/teams`)).pipe(
      map(arr => arr.map(x => new Team(x.id, x.name, x.status))),
      catchError(error => of(null))
    );
  }
  // create new course
  add(code: string, name: string, acronym: string, minTeamMembers: number, maxTeamMembers: number, enabled: boolean): Observable<boolean> {
    return this.http.post(url(`courses`), { code, name, acronym, minTeamMembers, maxTeamMembers, enabled }, httpOptions).pipe(
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
  // enable course
  enable(code: string): Observable<boolean> {
    return this.http.post(url(`courses/${code}/enable`), null, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  // disable course
  disable(code: string): Observable<boolean> {
    return this.http.post(url(`courses/${code}/disable`), null, httpOptions).pipe(
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
}
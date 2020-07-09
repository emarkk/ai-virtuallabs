import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { Course } from '../models/course.model';
import { Professor } from '../models/professor.model';
import { Team } from '../models/team.model';

import { url, httpOptions } from '../utils';

@Injectable({
  providedIn: 'root'
})
export class CourseService {
  private _insertionSuccessful: boolean = false;
  private _updateSuccessful: boolean = false;
  
  constructor(private http: HttpClient) {
  }

  public hasInsertedSuccessfully(): boolean {
    const value = this._insertionSuccessful;
    this._insertionSuccessful = false;
    return value;
  }
  public hasUpdatedSuccessfully(): boolean {
    const value = this._updateSuccessful;
    this._updateSuccessful = false;
    return value;
  }
  public insertionSuccessful() {
    this._insertionSuccessful = true;
  }
  public updateSuccessful() {
    this._updateSuccessful = true;
  }

  get(code: string): Observable<Course> {
    return this.http.get<Course>(url('courses/' + code)).pipe(
      map(x => new Course(x.code, x.name, x.acronym, x.minTeamMembers, x.maxTeamMembers, x.enabled)),
      catchError(error => of(null))
    );
  }
  getProfessors(code: string): Observable<Professor[]> {
    return this.http.get<Professor[]>(url('courses/' + code + '/professors')).pipe(
      map(arr => arr.map(x => new Professor(x.id, x.firstName, x.lastName, x.email, x.picturePath))),
      catchError(error => of(null))
    );
  }
  getTeams(code: string): Observable<Team[]> {
    return this.http.get<Team[]>(url('courses/' + code + '/teams')).pipe(
      map(arr => arr.map(x => new Team(x.id, x.name, x.status))),
      catchError(error => of(null))
    );
  }
  add(code: string, name: string, acronym: string, minTeamMembers: number, maxTeamMembers: number, enabled: boolean): Observable<boolean> {
    return this.http.post<boolean>(url('courses'), { code, name, acronym, minTeamMembers, maxTeamMembers, enabled }, httpOptions).pipe(
      catchError(error => of(null))
    );
  }
  update(code: string, name: string, acronym: string, minTeamMembers: number, maxTeamMembers: number, enabled: boolean): Observable<boolean> {
    return this.http.put(url('courses/' + code), { code, name, acronym, minTeamMembers, maxTeamMembers, enabled }, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(null))
    );
  }
  delete(code: string): Observable<boolean> {
    return this.http.delete(url('courses/' + code)).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  enable(code: string): Observable<boolean> {
    return this.http.post<boolean>(url('courses/' + code + '/enable'), null, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  disable(code: string): Observable<boolean> {
    return this.http.post<boolean>(url('courses/' + code + '/disable'), null, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
}
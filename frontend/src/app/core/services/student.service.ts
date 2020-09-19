import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of, forkJoin } from 'rxjs';
import { map, catchError, concatMap, reduce, flatMap } from 'rxjs/operators';

import { APIResult } from '../models/api-result.model';
import { Course } from '../models/course.model';
import { Student } from '../models/student.model';
import { Team, TeamInvitationStatus, TeamStatus } from '../models/team.model';

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
  // get student teams for a specific course
  getTeamsForCourse(studentId: number, courseCode: string): Observable<Team[]> {
    return this.http.get<any[]>(url(`students/${studentId}/teams?course=${courseCode}`)).pipe(
      // flatten Observable<Team[]> to Observable<Team>
      concatMap(teams => teams),
      // get team members status
      flatMap((team: any) => forkJoin(
        of(new Team(team.id, team.name, team.status as TeamStatus, null, new Date(team.invitationsExpiration), new Date(team.lastAction))),
        this.http.get<any[]>(url(`teams/${team.id}/members/status`)))
      ),
      // reconstruct Team object with members Map
      map(([team, members]: [Team, any[]]) => {
        team.members = [];
        for(let member of members) {
          const { id, firstName, lastName, email, hasPicture } = member.student;
          const student = new Student(id, firstName, lastName, email, hasPicture);
          team.members.push({ student , status: member.status as TeamInvitationStatus });
        }
        return team;
      }),
      // go back to Observable<Team[]>
      reduce((a, t) => a.concat(t), [])
    );
  }
  // get complete team for student for a specific course
  getJoinedTeamForCourse(studentId: number, courseCode: string): Observable<Team> {
    return this.getTeamsForCourse(studentId, courseCode).pipe(
      map(arr => arr.find(t => t.status == TeamStatus.COMPLETE))
    );
  }
  setProfilePicture(studentId: number, file: File): Observable<APIResult> {
    const formData: FormData = new FormData();
    formData.append('file', file);
    return this.http.post(url(`students/${studentId}/picture`), formData).pipe(
      map(res => APIResult.ok(res)),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
}
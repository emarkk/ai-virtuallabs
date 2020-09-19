import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of, forkJoin } from 'rxjs';
import { map, catchError, concatMap, flatMap, reduce, filter } from 'rxjs/operators';

import { APIResult } from '../models/api-result.model';
import { Page } from '../models/page.model';
import { Course } from '../models/course.model';
import { Student } from '../models/student.model';
import { EnrolledStudent } from '../models/enrolled-student.model';
import { Team, TeamStatus } from '../models/team.model';
import { Professor } from '../models/professor.model';
import { VmModel } from '../models/vmmodel.model';
import { Homework } from '../models/homework.model';
import { Vm } from '../models/vm.model';

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
  // get course teams
  getTeams(code: string): Observable<Team[]> {
    return this.http.get<any[]>(url(`courses/${code}/teams`)).pipe(
      map(arr => arr.map(x => new Team(x.id, x.name, x.status as TeamStatus, null, new Date(x.invitationExpiration), new Date(x.lastAction)))),
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
  // get course teams with relative vms
  getTeamsAndVms(code: string): Observable<{ team: Team, vm: Vm }[]> {
    return this.getTeams(code).pipe(
      concatMap(teams => teams),
      filter((team: Team) => team.status == TeamStatus.COMPLETE),
      flatMap((team: Team) => forkJoin([
        of(team),
        this.http.get<any[]>(url(`teams/${team.id}/vms`))
      ])),
      map(([team, vms]: [Team, any[]]) => {
        let teamsVms = { team, vms: [] };
        for(let vm of vms)
          teamsVms.vms.push(new Vm(vm.id, vm.vcpus, vm.diskSpace, vm.ram, vm.online, vm.owners, new Student(vm.creator.id, vm.creator.firstName, vm.creator.lastName, vm.creator.email, vm.creator.hasPicture)));
        return teamsVms;
      }),
      flatMap((teamVms: { team: Team, vms: Vm[] }) => of(teamVms.vms.length ? teamVms.vms.map(vm => ({ team: teamVms.team, vm })) : { team: teamVms.team, vm: null })),
      reduce((a, t) => a.concat(t), [])
    );
  }
  // get course homework assignments
  getHomeworks(code: string): Observable<Homework[]> {
    return this.http.get<any[]>(url(`courses/${code}/homeworks`)).pipe(
      map(arr => arr.map(x => new Homework(x.id, x.title, new Date(x.publicationDate), new Date(x.dueDate)))),
      catchError(error => of(null))
    );
  }
  // create new course
  add(code: string, name: string, acronym: string, minTeamMembers: number, maxTeamMembers: number, enabled: boolean): Observable<APIResult> {
    return this.http.post(url('courses'), { code, name, acronym, minTeamMembers, maxTeamMembers, enabled }, httpOptions).pipe(
      map(res => APIResult.ok(res)),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
  // add professor to course
  addProfessor(code: string, professorId: number): Observable<APIResult> {
    return this.http.post(url(`courses/${code}/professors`), { id: professorId }, httpOptions).pipe(
      map(res => APIResult.ok(res)),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
  // update course info
  update(code: string, name: string, acronym: string, minTeamMembers: number, maxTeamMembers: number, enabled: boolean): Observable<APIResult> {
    return this.http.put(url(`courses/${code}`), { code, name, acronym, minTeamMembers, maxTeamMembers, enabled }, httpOptions).pipe(
      map(res => APIResult.ok(res)),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
  // delete course
  delete(code: string): Observable<APIResult> {
    return this.http.delete(url(`courses/${code}`)).pipe(
      map(res => APIResult.ok(res)),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
  // enable/disable course
  setEnabled(code: string, enabled: boolean): Observable<APIResult> {
    const action = enabled ? 'enable' : 'disable';
    return this.http.post(url(`courses/${code}/${action}`), null, httpOptions).pipe(
      map(res => APIResult.ok(res)),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
  // enroll student to course
  enroll(code: string, studentId: number): Observable<APIResult> {
    return this.http.post(url(`courses/${code}/enroll`), { id: studentId }, httpOptions).pipe(
      map(res => APIResult.ok(res)),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
  // enroll students from CSV
  enrollFromCSV(code: string, csvFile: File): Observable<APIResult> {
    const formData: FormData = new FormData();
    formData.append('csvFile', csvFile);
    return this.http.post(url(`courses/${code}/enroll/csv`), formData).pipe(
      map(res => APIResult.ok(res)),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
  // unenroll subset of students from course
  unenroll(code: string, studentIds: number[]): Observable<APIResult> {
    return this.http.post(url(`courses/${code}/unenroll`), studentIds, httpOptions).pipe(
      map(res => APIResult.ok(res)),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
  // unenroll all students from course
  unenrollAll(code: string): Observable<APIResult> {
    return this.http.post(url(`courses/${code}/unenroll/all`), null, httpOptions).pipe(
      map(res => APIResult.ok(res)),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
}
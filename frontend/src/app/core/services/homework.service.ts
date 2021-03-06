import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { Homework } from '../models/homework.model';
import { Student } from '../models/student.model';
import { Page } from '../models/page.model';
import { HomeworkAction, HomeworkActionType } from '../models/homework-action.model';

import { ImageService } from './image.service';

import { url } from '../utils';
import { APIResult } from '../models/api-result.model';

@Injectable({
  providedIn: 'root'
})
export class HomeworkService {
  
  constructor(private http: HttpClient, private imageService: ImageService) {
  }
  
  // get homework metadata by id
  get(id: number): Observable<Homework> {
    return this.http.get<any>(url(`homeworks/${id}`)).pipe(
      map(x => new Homework(x.id, x.title, new Date(x.publicationDate), new Date(x.dueDate))),
      catchError(error => of(null))
    );
  }
  // get homework assignment text
  getText(id: number): Observable<string> {
    return this.imageService.get(`homeworks/${id}/text`);
  }
  // get all homework actions for a specific student
  getStudentActions(id: number, studentId: number): Observable<HomeworkAction[]> {
    return this.http.get<any[]>(url(`homeworks/${id}/actions/${studentId}`)).pipe(
      map(arr => arr.map(x =>
        new HomeworkAction(
          x.id,
          x.date ? new Date(x.date) : null,
          x.mark,
          x.actionType as HomeworkActionType,
          (x.actionType == HomeworkActionType.DELIVERY || x.actionType == HomeworkActionType.REVIEW) ? this.getActionResource(x.id) : null,
          new Student(x.student.id, x.student.firstName, x.student.lastName, x.student.email, x.student.hasPicture)
        ))),
      catchError(error => of(null))
    );
  }
  // get last action for course students
  getStudentsLastActions(id: number, filterBy: string, pageIndex: number = 0, pageSize: number = 15): Observable<Page<HomeworkAction>> {
    let params = new HttpParams().set('page', pageIndex.toString()).set('pageSize', pageSize.toString());
    if(filterBy)
      params = params.set('filterBy', filterBy);
    return this.http.get<any>(url(`homeworks/${id}/actions`), { params }).pipe(
      map(x => new Page(x.total, x.page.map(a =>
        new HomeworkAction(
          a.id,
          a.date ? new Date(a.date) : null,
          a.mark,
          a.actionType as HomeworkActionType,
          (a.actionType == HomeworkActionType.DELIVERY || a.actionType == HomeworkActionType.REVIEW) ? this.getActionResource(a.id) : null,
          new Student(a.student.id, a.student.firstName, a.student.lastName, a.student.email, a.student.hasPicture)
        )
      ))),
      catchError(error => of(null))
    );
  }
  // get image related to homework action (delivery or review)
  getActionResource(actionId: number): Observable<string> {
    return this.imageService.get(`homeworks/actions/${actionId}/resource`);
  }
  // create a new homework
  add(title: string, dueDate: number, file: File, courseCode: string): Observable<APIResult> {      
    const formData: FormData = new FormData();
    formData.append('title', title);
    formData.append('dueDate', `${dueDate}`);
    formData.append('file', file);
    formData.append('courseCode', courseCode);
    return this.http.post(url('homeworks'), formData).pipe(
      map(res => APIResult.ok(res)),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
  // submit a solution for the homework
  submitSolution(id: number, file: File): Observable<APIResult> {    
    const formData: FormData = new FormData();
    formData.append('id', `${id}`);
    formData.append('file', file);
    return this.http.post(url(`homeworks/${id}/delivery`), formData).pipe(
      map(res => APIResult.ok(res)),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
  // post a review for homework
  postReview(id: number, actionId: number, file: File, mark?: number): Observable<APIResult> {  
    const formData: FormData = new FormData();
    formData.append('id', `${id}`);
    formData.append('file', file);
    if(mark)
      formData.append('mark', `${mark}`);
    return this.http.post(url(`homeworks/${id}/review/${actionId}`), formData).pipe(
      map(res => APIResult.ok(res)),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
  // delete homework
  delete(id: number): Observable<APIResult> {
    return this.http.delete(url(`homeworks/${id}`)).pipe(
      map(res => APIResult.ok(res)),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
}
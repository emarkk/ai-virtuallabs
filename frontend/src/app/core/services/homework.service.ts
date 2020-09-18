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

@Injectable({
  providedIn: 'root'
})
export class HomeworkService {
  
  constructor(private http: HttpClient, private imageService: ImageService) {
  }
  
  get(id: number): Observable<Homework> {
    return this.http.get<any>(url(`homeworks/${id}`)).pipe(
      map(x => new Homework(x.id, x.title, new Date(x.publicationDate), new Date(x.dueDate))),
      catchError(error => of(null))
    );
  }
  getText(id: number): Observable<string> {
    return this.imageService.get(`homeworks/${id}/text`);
  }
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
  getActionResource(actionId: number): Observable<string> {
    return this.imageService.get(`homeworks/actions/${actionId}/resource`);
  }
  add(title: string, dueDate: number, file: File, courseCode: string): Observable<boolean> {      
    const formData: FormData = new FormData();
    formData.append('title', title);
    formData.append('dueDate', dueDate + '');
    formData.append('file', file);
    formData.append('courseCode', courseCode);
    return this.http.post(url('homeworks'), formData).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  submitSolution(id: number, file: File): Observable<boolean> {    
    const formData: FormData = new FormData();
    formData.append('id', id + '');
    formData.append('file', file);
    return this.http.post(url(`homeworks/${id}/delivery`), formData).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  delete(id: number): Observable<boolean> {
    return this.http.delete(url(`homeworks/${id}`)).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
}
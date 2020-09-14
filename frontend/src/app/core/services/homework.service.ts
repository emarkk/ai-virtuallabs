import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { Homework } from '../models/homework.model';

import { url, httpOptions } from '../utils';

@Injectable({
  providedIn: 'root'
})
export class HomeworkService {
  
  constructor(private http: HttpClient) {
  }
  
  get(id: number): Observable<Homework> {
    return this.http.get<Homework>(url(`homeworks/${id}`)).pipe(
      catchError(error => of(null))
    );
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
}
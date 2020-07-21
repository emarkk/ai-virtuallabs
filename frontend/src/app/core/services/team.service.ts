import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { url, httpOptions } from '../utils';

@Injectable({
  providedIn: 'root'
})
export class TeamService {
  
  constructor(private http: HttpClient) {
  }

  // propose a new team
  propose(name: string, timeout: number, membersIds: number[], courseCode: string): Observable<boolean> {
    return this.http.post(url('teams'), { name, timeout, membersIds, courseCode }, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  
}
import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { url, httpOptions } from '../utils';

const ONE_HOUR = 60*60*1000;

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private token: string = null;
  private expiration: number = null;

  constructor(private http: HttpClient) {
  }

  isLogged() {
    return !!this.token && Date.now() < this.expiration;
  }
  login(username: string, password: string): Observable<boolean> {
    return this.http.post(url('auth'), { username, password }, httpOptions).pipe(
      map((x: any) => {
        this.token = x.token;
        this.expiration = Date.now() + ONE_HOUR;
        return true;
      }),
      catchError(error => of(false))
    );
  }
  logout(): void {
    this.token = null;
  }
  authorizeRequest(request: HttpRequest<any>): HttpRequest<any> {
    if(!this.isLogged())
      return request;

    return request.clone({
      setHeaders: {
        Authorization: 'Bearer ' + this.token
      }
    });
  }

}

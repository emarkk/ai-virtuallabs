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

  constructor(private http: HttpClient) {
  }

  isLogged() {
    const { token, expiration } = this.getToken();
    return !!token && Date.now() < expiration;
  }
  getUserData() {
    const { token, expiration } = this.getToken();

    if(!token || Date.now() >= expiration)
      return null;

    const data = JSON.parse(atob(token.split('.')[1]));
    return { username: data.sub, roles: data.roles };
  }
  login(username: string, password: string): Observable<boolean> {
    return this.http.post(url('auth'), { username, password }, httpOptions).pipe(
      map((x: any) => {
        this.setToken(x.token, Date.now() + ONE_HOUR);
        return true;
      }),
      catchError(error => of(false))
    );
  }
  logout(): void {
    this.deleteToken();
  }
  authorizeRequest(request: HttpRequest<any>): HttpRequest<any> {
    if(!this.isLogged())
      return request;

    return request.clone({
      setHeaders: {
        Authorization: 'Bearer ' + this.getToken().token
      }
    });
  }
  private getToken(): { token: string, expiration: number } {
    const token = localStorage.getItem('jwt_token');
    const expiration = parseInt(localStorage.getItem('jwt_expiration'));
    return { token, expiration };
  }
  private setToken(token: string, expiration: number): void {
    localStorage.setItem('jwt_token', token);
    localStorage.setItem('jwt_expiration', expiration.toString());
  }
  private deleteToken(): void {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('jwt_expiration');
  }
}

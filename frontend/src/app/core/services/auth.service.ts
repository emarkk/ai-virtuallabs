import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { SignalService } from './signal.service';

import { url, httpOptions } from '../utils';
import { APIResult } from '../models/api-result.model';

const ONE_HOUR = 60*60*1000;

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient, private signalService: SignalService) {
  }

  // check if user is logged in
  isLogged() {
    const { token, expiration } = this.getToken();
    // token must exist and must not be expired
    const logged = !!token && Date.now() < expiration;

    if(logged)
      // connect websocket if needed
      this.signalService.initWebsocket(token);
    else
      // disconnect websocket
      this.signalService.releaseWebsocket();
    
    return logged;
  }
  // get user id
  getId() {
    return this.getUserData().id;
  }
  // extract user info from JWT token
  getUserData() {
    const { token, expiration } = this.getToken();

    // if token is null or expired, no info is returned
    if(!token || Date.now() >= expiration)
      return null;

    // base64-decode token payload (2nd part, since JWT = $header.$payload.$signature)
    const data = JSON.parse(atob(token.split('.')[1]));
    // return user info (username, id, roles)
    return { username: data.sub, id: parseInt(data.sub.substring(1)), roles: data.roles };
  }
  login(username: string, password: string): Observable<APIResult> {
    return this.http.post(url('auth'), { username, password }, httpOptions).pipe(
      map((res: any) => {
        this.setToken(res.token, Date.now() + ONE_HOUR);
        return APIResult.ok(res);
      }),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
  logout(): void {
    this.deleteToken();
    // disconnect websocket
    this.signalService.releaseWebsocket();
  }
  authorizeRequest(request: HttpRequest<any>): HttpRequest<any> {
    // if not logged in, simply return original request
    if(!this.isLogged())
      return request;

    // otherwise, clone original request and add JWT authorization header
    return request.clone({
      setHeaders: {
        Authorization: `Bearer ${this.getToken().token}`
      }
    });
  }
  // get token data from LocalStorage
  private getToken(): { token: string, expiration: number } {
    const token = localStorage.getItem('jwt_token');
    const expiration = parseInt(localStorage.getItem('jwt_expiration'));
    return { token, expiration };
  }
  // save token data in LocalStorage
  private setToken(token: string, expiration: number): void {
    localStorage.setItem('jwt_token', token);
    localStorage.setItem('jwt_expiration', expiration.toString());
  }
  // delete token data from LocalStorage
  private deleteToken(): void {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('jwt_expiration');
  }
}

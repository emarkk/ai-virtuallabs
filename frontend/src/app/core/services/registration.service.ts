import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { APIResult } from '../models/api-result.model';

import { url, httpOptions } from '../utils';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {
  // flag to store if user has just registered successfully
  // (so that /signup/success can be reached)
  private _registrationSuccessful: boolean = false;

  constructor(private http: HttpClient) {
  }

  // get flag value and reset it at the same time
  public hasRegisteredSuccessfully(): boolean {
    const value = this._registrationSuccessful;
    this._registrationSuccessful = false;
    return value;
  }

  // set by signup page when registration is successful
  public registrationSuccessful() {
    this._registrationSuccessful = true;
  }

  // register user
  public signup(firstName: String, lastName: String, matricola: String, email: String, password: String): Observable<APIResult> {
    return this.http.post(url('signup'), { firstName, lastName, id: parseInt(matricola.substring(1)), email, password }, httpOptions).pipe(
      map(res => APIResult.ok(res)),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
  // confirm user
  public confirm(token: String): Observable<APIResult> {
    return this.http.get<boolean>(url(`signup/confirm/${token}`)).pipe(
      map(res => APIResult.ok(res)),
      catchError(res => of(APIResult.error(res.error.message)))
    );
  }
}

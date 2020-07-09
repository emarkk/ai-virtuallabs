import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { url, httpOptions } from '../utils';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {
  private _registrationSuccessful: boolean = false;

  constructor(private http: HttpClient) {
  }

  public hasRegisteredSuccessfully(): boolean {
    const value = this._registrationSuccessful;
    this._registrationSuccessful = false;
    return value;
  }

  public registrationSuccessful() {
    this._registrationSuccessful = true;
  }

  public signup(firstName: String, lastName: String, matricola: String, email: String, password: String): Observable<boolean> {
    return this.http.post(url('signup'),{ firstName, lastName, id: parseInt(matricola.substring(1)), email, password }, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
  public confirm(token: String): Observable<boolean> {
    return this.http.get<boolean>(url('signup/confirm/' + token)).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
}

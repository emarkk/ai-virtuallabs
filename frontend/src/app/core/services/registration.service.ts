import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

import { url, httpOptions } from '../utils';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {
  private registrationSuccessful: Boolean = false;

  constructor(private http: HttpClient) {
  }

  public hasRegisteredSuccessfully(): Boolean {
    return this.registrationSuccessful;
  }

  public setRegistrationSuccessful(value: Boolean) {
    this.registrationSuccessful = value;
  }

  public signup(firstName: String, lastName: String, matricola: String, email: String, password: String): Observable<Boolean> {
    return this.http.post(url('signup'),{ firstName, lastName, id: parseInt(matricola.substring(1)), email, password }, httpOptions).pipe(
      map(_ => true),
      catchError(error => of(false))
    );
  }
}

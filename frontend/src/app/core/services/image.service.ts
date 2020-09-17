import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Observer, of } from 'rxjs';
import { catchError, flatMap } from 'rxjs/operators';

import { url } from '../utils';

@Injectable({
  providedIn: 'root'
})
export class ImageService {
  
  constructor(private http: HttpClient) {
  }
  
  get(imageURL: string): Observable<string> {
    return this.http.get(url(imageURL), { responseType: 'blob' }).pipe(
      flatMap(image => this.toBase64(image)),
      catchError(error => of(null))
    );
  }
  getProfessorProfilePicture(id: number): Observable<string> {
    return this.http.get(url(`professors/${id}/picture`), { responseType: 'blob' }).pipe(
      flatMap(image => this.toBase64(image)),
      catchError(error => of(null))
    );
  }
  getStudentProfilePicture(id: number): Observable<string> {
    return this.http.get(url(`students/${id}/picture`), { responseType: 'blob' }).pipe(
      flatMap(image => this.toBase64(image)),
      catchError(error => of(null))
    );
  }
  
  private toBase64(blob: Blob): Observable<string> {
    return new Observable((observer: Observer<string>) => {
      var reader = new FileReader(); 
      reader.readAsDataURL(blob); 
      reader.onloadend = function() {
        observer.next(reader.result.toString());
        observer.complete();
      }
    });
  }
}
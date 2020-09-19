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
  
  // get image at a specific url
  get(imageURL: string): Observable<string> {
    return this.http.get(url(imageURL), { responseType: 'blob' }).pipe(
      flatMap(image => this.toBase64(image)),
      catchError(error => of(null))
    );
  }
  // get professor profile picture
  getProfessorProfilePicture(id: number): Observable<string> {
    return this.http.get(url(`professors/${id}/picture`), { responseType: 'blob' }).pipe(
      flatMap(image => this.toBase64(image)),
      catchError(error => of(null))
    );
  }
  // get student profile picture
  getStudentProfilePicture(id: number): Observable<string> {
    return this.http.get(url(`students/${id}/picture`), { responseType: 'blob' }).pipe(
      flatMap(image => this.toBase64(image)),
      catchError(error => of(null))
    );
  }
  
  // encode blob to base64 (to be used in img src)
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
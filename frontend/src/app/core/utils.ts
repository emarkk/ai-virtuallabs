import { HttpHeaders } from '@angular/common/http';

export const url = path => 'http://localhost:3000/api/' + path;
export const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json',
  })
};
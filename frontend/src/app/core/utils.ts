import { HttpHeaders } from '@angular/common/http';

export const url = path => 'http://localhost:3000/' + path;
export const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json',
  })
};
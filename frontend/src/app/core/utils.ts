import { HttpHeaders } from '@angular/common/http';

export const serverUrl = 'http://localhost:3000/';
export const url = path => `http://localhost:3000/api/${path}`;

export const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json',
  })
};

export const timeString = ms => {
  if(ms < 0)
    return null;
    
  const days = Math.floor(ms / (24*60*60*1000));
  const hrs = Math.floor((ms % (24*60*60*1000)) / (60*60*1000));
  const mins = Math.floor((ms % (60*60*1000)) / (60*1000));
  const secs = Math.floor((ms % (60*1000)) / 1000);
  if(days > 0)
    return `${days}d ${hrs}h`;
  if(hrs > 0)
    return `${hrs}h ${mins}m`;
  if(mins > 0)
    return `${mins}m ${secs}s`;
  return `${secs}s`;
};
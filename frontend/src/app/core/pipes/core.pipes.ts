import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'zeroPad' })
export class ZeroPadPipe implements PipeTransform {
  transform(input: any, length?: number): string {
    length = isNaN(length) ? 5 : length;
    return ('0'.repeat(length) + input).slice(-length);
  }
}
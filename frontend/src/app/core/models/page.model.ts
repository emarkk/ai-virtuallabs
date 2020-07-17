export class Page<T> {
  count: number;
  data: T[];

  constructor(count: number, data: T[]) {
    this.count = count;
    this.data = data;
  }
}
import { DataSource } from '@angular/cdk/table';

export interface PagingSortingDataSource<T> extends DataSource<T> {
  readonly size: number;
  readonly maxSize: number;
  readonly loaded: boolean;
  readonly currentPage: T[];
  loadData(sortBy?: string, sortDirection?: string, pageIndex?: number, pageSize?: number, filters?: Array<any>);
}
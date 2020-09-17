import { Observable, BehaviorSubject } from 'rxjs';
import { CollectionViewer } from '@angular/cdk/collections';

import { HomeworkAction } from '../models/homework-action.model';

import { HomeworkService } from '../services/homework.service';

import { PagingSortingDataSource } from './pagingsorting.datasource';

export class HomeworkOverviewDataSource implements PagingSortingDataSource<HomeworkAction> {
  private homeworkId: number;
  private datasetSize: number;
  private lastActionsSubject = new BehaviorSubject<HomeworkAction[]>([]);
  
  constructor(private homeworkService: HomeworkService, homeworkId: number) {
    this.homeworkId = homeworkId;
  }

  connect(collectionViewer: CollectionViewer): Observable<HomeworkAction[]> {
    return this.lastActionsSubject.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.lastActionsSubject.complete();
  }

  get size() {
    return this.datasetSize;
  }

  get currentPage() {
    return this.lastActionsSubject.value;
  }

  loadData(sortBy: string = null, sortDirection: string = null, pageIndex: number = 0, pageSize: number = 15) {
    this.homeworkService.getStudentsLastActions(this.homeworkId, pageIndex, pageSize).subscribe(page => {
      this.datasetSize = page.count;
      this.lastActionsSubject.next(page.data)
    });
  }
}
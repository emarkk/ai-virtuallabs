import { Observable, BehaviorSubject } from 'rxjs';
import { CollectionViewer } from '@angular/cdk/collections';

import { EnrolledStudent } from '../models/enrolled-student.model';

import { CourseService } from '../services/course.service';

import { PagingSortingDataSource } from './pagingsorting.datasource';

export class EnrolledStudentsDataSource implements PagingSortingDataSource<EnrolledStudent> {
  private courseCode: string;
  private datasetSize: number;
  private studentsSubject = new BehaviorSubject<EnrolledStudent[]>([]);
  
  constructor(private courseService: CourseService, courseCode: string) {
    this.courseCode = courseCode;
  }

  connect(collectionViewer: CollectionViewer): Observable<EnrolledStudent[]> {
    return this.studentsSubject.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.studentsSubject.complete();
  }

  get size() {
    return this.datasetSize;
  }

  get currentPage() {
    return this.studentsSubject.value;
  }

  loadData(sortBy: string = null, sortDirection: string = null, pageIndex: number = 0, pageSize: number = 15) {
    this.courseService.getStudents(this.courseCode, sortBy, sortDirection, pageIndex, pageSize).subscribe(page => {
      this.datasetSize = page.count;
      this.studentsSubject.next(page.data)
    });
  }
}
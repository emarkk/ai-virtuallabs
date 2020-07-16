import { Observable, BehaviorSubject } from 'rxjs';
import { CollectionViewer, DataSource } from '@angular/cdk/collections';

import { Student } from '../models/student.model';

import { CourseService } from '../services/course.service';

import { PagingSortingDataSource } from './pagingsorting.datasource';

export class EnrolledStudentsDataSource implements PagingSortingDataSource<Student> {
  private metadata: any = {};
  private studentsSubject = new BehaviorSubject<Student[]>([]);
  
  constructor(private courseService: CourseService) {
  }

  connect(collectionViewer: CollectionViewer): Observable<Student[]> {
    return this.studentsSubject.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.studentsSubject.complete();
  }

  get size() {
    return this.metadata.studentCount;
  }

  get currentPage() {
    return this.studentsSubject.value;
  }

  loadMetadata(courseCode: string): void {
    this.metadata = {
      courseCode,
      studentCount: 15
    };
  }

  loadData(sortBy: string = null, sortDirection: string = null, pageIndex: number = 0, pageSize: number = 15) {
    this.courseService.getStudents(this.metadata.courseCode, sortBy, sortDirection, pageIndex, pageSize).subscribe(students =>
      this.studentsSubject.next(students)
    );
  }
}
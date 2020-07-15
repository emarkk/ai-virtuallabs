import { Observable, BehaviorSubject } from 'rxjs';
import { CollectionViewer } from '@angular/cdk/collections';

import { Student } from '../models/student.model';

import { CourseService } from '../services/course.service';

import { StudentsDataSource } from './students.datasource';

export class EnrolledStudentsDataSource implements StudentsDataSource {
  private metadata: any = {};
  private studentsSubject = new BehaviorSubject<Student[]>([]);
  
  constructor(private courseService: CourseService) {}

    connect(collectionViewer: CollectionViewer): Observable<Student[]> {
      return this.studentsSubject.asObservable();
    }

    disconnect(collectionViewer: CollectionViewer): void {
      this.studentsSubject.complete();
    }

    getLength() {
      return this.metadata.studentCount;
    }

    loadMetadata(courseCode: string): void {
      this.metadata = {
        courseCode,
        studentCount: 15
      };
    }

    loadStudents(sortField: string = null, sortDirection: string = null, pageIndex: number = 0, pageSize: number = 15) {
      this.courseService.getStudents(this.metadata.courseCode, sortField, sortDirection, pageIndex, pageSize).subscribe(students =>
        this.studentsSubject.next(students)
      );
    }
}
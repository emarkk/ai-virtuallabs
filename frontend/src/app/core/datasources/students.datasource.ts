import { DataSource } from '@angular/cdk/table';

import { Student } from '../models/student.model';

export interface StudentsDataSource extends DataSource<Student> {
  getLength();
  loadStudents(sortField?: string, sortDirection?: string, pageIndex?: number, pageSize?: number);
}
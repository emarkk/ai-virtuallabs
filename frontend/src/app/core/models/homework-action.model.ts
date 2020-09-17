import { Observable } from 'rxjs';

import { Student } from './student.model';

export enum HomeworkActionType {
  NULL = 'NULL',
  READ = 'READ',
  DELIVERY = 'DELIVERY',
  REVIEW = 'REVIEW'
};

export class HomeworkAction {
  id: number;
  date: Date;
  mark: number;
  actionType: HomeworkActionType;
  resource$: Observable<string>;
  student: Student;

  constructor(id: number, date: Date, mark: number, actionType: HomeworkActionType, resource$: Observable<string>, student: Student) {
    this.id = id;
    this.date = date;
    this.mark = mark;
    this.actionType = actionType;
    this.resource$ = resource$;
    this.student = student;
  }
}
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
  student: Student;

  constructor(id: number, date: Date, mark: number, actionType: HomeworkActionType, student: Student) {
    this.id = id;
    this.date = date;
    this.mark = mark;
    this.actionType = actionType;
    this.student = student;
  }
}
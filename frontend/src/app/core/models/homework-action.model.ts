import { Student } from './student.model';

export enum HomeworkActionType {
  READ = 'READ',
  DELIVERY = 'DELIVERY',
  REVIEW = 'REVIEW'
};

export class HomeworkAction {
  id: number;
  date: Date;
  actionType: HomeworkActionType;
  student: Student;

  constructor(id: number, date: Date, actionType: HomeworkActionType, student: Student) {
    this.id = id;
    this.date = date;
    this.actionType = actionType;
    this.student = student;
  }
}
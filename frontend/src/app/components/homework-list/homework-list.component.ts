import { Component, OnInit, Input } from '@angular/core';

import { Homework } from 'src/app/core/models/homework.model';

@Component({
  selector: 'app-homework-list',
  templateUrl: './homework-list.component.html',
  styleUrls: ['./homework-list.component.css']
})
export class HomeworkListComponent implements OnInit {
  homeworkList: Homework[] = null;
  activeHomeworkList: Homework[] = null;
  pastHomeworkList: Homework[] = null;

  @Input() set homeworks(data: Homework[]) {
    this.homeworkList = data;
    if(data) {
      this.activeHomeworkList = this.homeworkList.filter(h => h.dueDate > new Date());
      this.pastHomeworkList = this.homeworkList.filter(h => h.dueDate < new Date());
    }
  }

  constructor() {
  }

  ngOnInit(): void {
    setInterval(() => {
      if(this.homeworkList) {
        this.activeHomeworkList = this.homeworkList.filter(h => h.dueDate > new Date());
        this.pastHomeworkList = this.homeworkList.filter(h => h.dueDate < new Date());
      }
    }, 60*1000);
  }

}
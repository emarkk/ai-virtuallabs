import { Component, OnInit, Input } from '@angular/core';

import { Homework } from 'src/app/core/models/homework.model';

@Component({
  selector: 'app-homework-list',
  templateUrl: './homework-list.component.html',
  styleUrls: ['./homework-list.component.css']
})
export class HomeworkListComponent implements OnInit {
  showOnlyActive: boolean = false;
  
  homeworkList: Homework[] = null;
  activeHomeworkList: Homework[] = null;
  pastHomeworkList: Homework[] = null;

  @Input() set homeworks(data: Homework[]) {
    this.homeworkList = data;
    if(data) {
      this.activeHomeworkList = this.homeworkList.filter(h => h.dueDate > new Date());
      this.pastHomeworkList = this.homeworkList.filter(h => h.dueDate < new Date());
      // for active homeworks, we want the closest one (due date most forthcoming in future) to appear first
      this.activeHomeworkList.sort((a, b) => a.dueDate.getTime() - b.dueDate.getTime());
      // for past homeworks, we want the closest one (due date most forthcoming in past) to appear first
      this.pastHomeworkList.sort((a, b) => b.dueDate.getTime() - a.dueDate.getTime());
    }
  }
  @Input() set onlyActive(value: boolean) {
    this.showOnlyActive = value;
  }

  constructor() {
  }

  ngOnInit(): void {
    setInterval(() => {
      if(this.homeworkList) {
        this.activeHomeworkList = this.homeworkList.filter(h => h.dueDate > new Date());
        this.pastHomeworkList = this.homeworkList.filter(h => h.dueDate < new Date());
        this.activeHomeworkList.sort((a, b) => a.dueDate.getTime() - b.dueDate.getTime());
        this.pastHomeworkList.sort((a, b) => b.dueDate.getTime() - a.dueDate.getTime());
      }
    }, 60*1000);
  }

}
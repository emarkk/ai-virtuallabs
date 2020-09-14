import { Component, OnInit, Input } from '@angular/core';

import { Homework } from 'src/app/core/models/homework.model';

@Component({
  selector: 'app-homework-list',
  templateUrl: './homework-list.component.html',
  styleUrls: ['./homework-list.component.css']
})
export class HomeworkListComponent implements OnInit {
  homeworkList: Homework[] = null;

  @Input() set homeworks(data: Homework[]) {
    this.homeworkList = data;
  }

  constructor() {
  }

  ngOnInit(): void {
  }

}
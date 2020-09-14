import { Component, OnInit, Input } from '@angular/core';

import { Homework } from 'src/app/core/models/homework.model';

@Component({
  selector: 'app-homework-item',
  templateUrl: './homework-item.component.html',
  styleUrls: ['./homework-item.component.css']
})
export class HomeworkItemComponent implements OnInit {
  homework: Homework;

  @Input() set data(value: Homework) {
    this.homework = value;
  }

  constructor() {
  }

  ngOnInit(): void {
  }

}
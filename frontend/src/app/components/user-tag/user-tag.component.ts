import { Component, OnInit, Input } from '@angular/core';

import { Professor } from 'src/app/core/models/professor.model';
import { Student } from 'src/app/core/models/student.model';

@Component({
  selector: 'app-user-tag',
  templateUrl: './user-tag.component.html',
  styleUrls: ['./user-tag.component.css']
})
export class UserTagComponent implements OnInit {
  name: string;
  boldName: string;
  info: string;
  u: Student|Professor;

  @Input() set firstName(value: string) {
    this.name = value;
  }
  @Input() set lastName(value: string) {
    this.boldName = value;
  }
  @Input() set id(value: string) {
    this.info = value;
  }
  @Input() set user(value: Student|Professor) {
    this.u = value;
  }

  constructor() {
  }

  ngOnInit(): void {
  }

}
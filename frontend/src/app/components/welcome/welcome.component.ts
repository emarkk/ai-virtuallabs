import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

import { Professor } from 'src/app/core/models/professor.model';
import { Student } from 'src/app/core/models/student.model';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {
  fullName: string;
  u: Student|Professor;

  @Input() set name(value: string) {
    this.fullName = value;
  }
  @Input() set user(value: Student|Professor) {
    this.u = value;
  }

  @Output() editPictureClick = new EventEmitter<void>();

  constructor() {
  }

  ngOnInit(): void {
  }

}
import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {
  fullName: string;

  @Input() set name(value: string) {
    this.fullName = value;
  }

  constructor() {
  }

  ngOnInit(): void {
  }

}
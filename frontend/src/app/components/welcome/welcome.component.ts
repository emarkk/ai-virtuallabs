import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {
  fullName: string;
  picturePath: string;

  @Input() set name(value: string) {
    this.fullName = value;
  }
  @Input() set picture(value: string) {
    this.picturePath = value;
  }

  constructor() {
  }

  ngOnInit(): void {
  }

}
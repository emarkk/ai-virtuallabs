import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-user-tag',
  templateUrl: './user-tag.component.html',
  styleUrls: ['./user-tag.component.css']
})
export class UserTagComponent implements OnInit {
  name: string;
  boldName: string;
  picturePath: string;

  @Input() set firstName(value: string) {
    this.name = value;
  }
  @Input() set lastName(value: string) {
    this.boldName = value;
  }
  @Input() set picture(value: string) {
    this.picturePath = value;
  }

  constructor() {
  }

  ngOnInit(): void {
  }

}
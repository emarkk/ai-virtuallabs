import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent implements OnInit {
  // navigation information
  data: Array<any>;

  @Input() set navigationData(data: Array<any>) {
    this.data = data;
  }

  constructor() {
  }

  ngOnInit(): void {
  }

}
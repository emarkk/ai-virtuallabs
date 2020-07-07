import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-tile',
  templateUrl: './tile.component.html',
  styleUrls: ['./tile.component.css']
})
export class TileComponent implements OnInit {
  tileTitle: String = null;

  @Input() set title(value: String) {
    this.tileTitle = value;
  }

  constructor() {
  }

  ngOnInit(): void {
  }

}
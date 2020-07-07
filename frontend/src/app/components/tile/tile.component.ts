import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-tile',
  templateUrl: './tile.component.html',
  styleUrls: ['./tile.component.css']
})
export class TileComponent implements OnInit {
  tileIcon: string;
  tileTitle: string;
  tileTitleLink: string;

  @Input() set icon(name: string) {
    this.tileIcon = name;
  }
  @Input() set title(value: string) {
    this.tileTitle = value;
  }
  @Input() set titleLink(value: string) {
    this.tileTitleLink = value;
  }

  constructor() {
  }

  ngOnInit(): void {
  }

}
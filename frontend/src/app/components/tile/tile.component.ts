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
  tileNoPadding: boolean;

  @Input() set icon(name: string) {
    this.tileIcon = name;
  }
  @Input() set text(value: string) {
    this.tileTitle = value;
  }
  @Input() set textLink(value: string) {
    this.tileTitleLink = value;
  }
  @Input() set noPad(value: boolean) {
    this.tileNoPadding = true;
  }

  constructor() {
  }

  ngOnInit(): void {
  }

}
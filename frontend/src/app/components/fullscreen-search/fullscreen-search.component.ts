import { Component, OnInit, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-fullscreen-search',
  templateUrl: './fullscreen-search.component.html',
  styleUrls: ['./fullscreen-search.component.css']
})
export class FullscreenSearchComponent implements OnInit {

  @Output() search = new EventEmitter<string>();
  @Output() close = new EventEmitter<void>();

  constructor() {
  }

  ngOnInit(): void {
  }

  searchInputChanged(value: string) {
    this.search.emit(value);
  }
  closeButtonClicked() {
    this.close.emit();
  }
}
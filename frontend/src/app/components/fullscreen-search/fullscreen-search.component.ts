import { Component, OnInit, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { MatInput } from '@angular/material/input';

@Component({
  selector: 'app-fullscreen-search',
  templateUrl: './fullscreen-search.component.html',
  styleUrls: ['./fullscreen-search.component.css']
})
export class FullscreenSearchComponent implements OnInit {
  searchMatches: any[] = null;

  @Input() set matches(data: any[]) {
    this.searchMatches = data;
  }

  @Output() search = new EventEmitter<string>();
  @Output() selectResult = new EventEmitter<number>();
  @Output() close = new EventEmitter<void>();

  @ViewChild(MatInput)
  seachInput: MatInput;

  constructor() {
  }

  ngOnInit(): void {
  }

  searchInputChanged(value: string) {
    this.search.emit(value);
  }
  matchItemSelected(id: number) {
    this.seachInput.value = '';
    this.selectResult.emit(id);
  }
  closeButtonClicked() {
    this.seachInput.value = '';
    this.close.emit();
  }
}
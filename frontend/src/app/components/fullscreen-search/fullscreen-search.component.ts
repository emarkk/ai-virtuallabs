import { Component, OnInit, Input, Output, EventEmitter, ViewChild, ElementRef } from '@angular/core';
import { MatInput } from '@angular/material/input';

@Component({
  selector: 'app-fullscreen-search',
  templateUrl: './fullscreen-search.component.html',
  styleUrls: ['./fullscreen-search.component.css']
})
export class FullscreenSearchComponent implements OnInit {
  // results that match search query
  searchMatches: any[] = null;
  
  @ViewChild(MatInput)
  seachInput: MatInput;

  @ViewChild('searchRef')
  seachInputRef: ElementRef;

  @Input() set matches(data: any[]) {
    this.searchMatches = data;
  }
  @Input() set focus(value: boolean) {
    // fullscreen search is now visible, so focus input element
    setTimeout(() => {
      this.seachInputRef.nativeElement.focus();
    }, 0);
  }

  // current search query changed event
  @Output() search = new EventEmitter<string>();
  // search result clicked event
  @Output() selectResult = new EventEmitter<number>();
  // close button clicked event
  @Output() close = new EventEmitter<void>();

  constructor() {
  }

  ngOnInit(): void {
  }

  searchInputChanged(value: string) {
    // emit updated search query
    this.search.emit(value);
  }
  matchItemSelected(id: number) {
    // reset search input and emit selected result id
    this.seachInput.value = '';
    this.selectResult.emit(id);
  }
  closeButtonClicked() {
    // reset search input and emit close
    this.seachInput.value = '';
    this.close.emit();
  }
}
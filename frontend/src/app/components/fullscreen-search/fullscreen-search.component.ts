import { Component, OnInit, Input, Output, EventEmitter, ViewChild, ElementRef } from '@angular/core';
import { MatInput } from '@angular/material/input';

@Component({
  selector: 'app-fullscreen-search',
  templateUrl: './fullscreen-search.component.html',
  styleUrls: ['./fullscreen-search.component.css']
})
export class FullscreenSearchComponent implements OnInit {
  searchMatches: any[] = null;
  
  @ViewChild(MatInput)
  seachInput: MatInput;

  @ViewChild('searchRef')
  seachInputRef: ElementRef;

  @Input() set matches(data: any[]) {
    this.searchMatches = data;
  }
  @Input() set focus(value: boolean) {
    setTimeout(() => {
      this.seachInputRef.nativeElement.focus();
    }, 0);
  }

  @Output() search = new EventEmitter<string>();
  @Output() selectResult = new EventEmitter<number>();
  @Output() close = new EventEmitter<void>();

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
import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-async-button',
  templateUrl: './async-button.component.html',
  styleUrls: ['./async-button.component.css']
})
export class AsyncButtonComponent implements OnInit {
  showLoader: boolean = false;

  @Input() set loading(value: boolean) {
    this.showLoader = value;
  }

  @Output() click = new EventEmitter<MouseEvent>();

  constructor() {
  }

  ngOnInit(): void {
  }

  buttonClicked(event: MouseEvent) {
    this.click.emit(event);
  }

}
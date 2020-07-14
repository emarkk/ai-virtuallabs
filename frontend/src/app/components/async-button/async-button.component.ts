import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-async-button',
  templateUrl: './async-button.component.html',
  styleUrls: ['./async-button.component.css']
})
export class AsyncButtonComponent implements OnInit {
  showLoader: boolean = false;
  buttonColor: string = 'primary';

  @Input() set loading(value: boolean) {
    this.showLoader = value;
  }
  @Input() set color(value: string) {
    this.buttonColor = value;
  }

  @Output() aclick = new EventEmitter<MouseEvent>();

  constructor() {
  }

  ngOnInit(): void {
  }

  buttonClicked(event: MouseEvent) {
    this.aclick.emit(event);
  }

}
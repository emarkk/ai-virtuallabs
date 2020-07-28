import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

import { Vm } from 'src/app/core/models/vm.model';

@Component({
  selector: 'app-vm-item',
  templateUrl: './vm-item.component.html',
  styleUrls: ['./vm-item.component.css']
})
export class VmItemComponent implements OnInit {
  vm: Vm;

  @Input() set data(value: Vm) {
    this.vm = value;
  }

  @Output() addOwners = new EventEmitter<void>();
  @Output() delete = new EventEmitter<void>();

  constructor() {
  }

  ngOnInit(): void {
  }

  addOwnersButtonClicked() {
    this.addOwners.emit();
  }
  deleteButtonClicked() {
    this.delete.emit();
  }
}
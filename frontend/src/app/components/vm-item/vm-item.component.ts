import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Observable } from 'rxjs';

import { Vm } from 'src/app/core/models/vm.model';

@Component({
  selector: 'app-vm-item',
  templateUrl: './vm-item.component.html',
  styleUrls: ['./vm-item.component.css']
})
export class VmItemComponent implements OnInit {
  vm: Vm;
  owner: boolean;
  baseLink: string;

  @Input() set data(value: Vm) {
    this.vm = value;
    this.owner = false;
  }

  @Input() set connectLink(value: string) {
    this.baseLink = value;
  }

  @Output() toggleOnline = new EventEmitter<void>();
  @Output() addOwners = new EventEmitter<void>();
  @Output() delete = new EventEmitter<void>();

  constructor() {
  }

  ngOnInit(): void {
  }

  onlineSwitchClicked() {
    this.toggleOnline.emit();
  }
  addOwnersButtonClicked() {
    this.addOwners.emit();
  }
  deleteButtonClicked() {
    this.delete.emit();
  }
}
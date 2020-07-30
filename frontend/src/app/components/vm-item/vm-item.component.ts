import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

import { Vm } from 'src/app/core/models/vm.model';

import { AuthService } from 'src/app/core/services/auth.service';

@Component({
  selector: 'app-vm-item',
  templateUrl: './vm-item.component.html',
  styleUrls: ['./vm-item.component.css']
})
export class VmItemComponent implements OnInit {
  vm: Vm;
  owner: boolean;

  @Input() set data(value: Vm) {
    this.vm = value;
    this.owner = this.vm.ownersIds.includes(this.authService.getId());
  }

  @Output() toggleOnline = new EventEmitter<void>();
  @Output() connect = new EventEmitter<void>();
  @Output() edit = new EventEmitter<void>();
  @Output() addOwners = new EventEmitter<void>();
  @Output() delete = new EventEmitter<void>();

  constructor(private authService: AuthService) {
  }

  ngOnInit(): void {
  }

  onlineSwitchClicked() {
    this.toggleOnline.emit();
  }
  connectButtonClicked() {
    this.connect.emit();
  }
  editButtonClicked() {
    this.edit.emit();
  }
  addOwnersButtonClicked() {
    this.addOwners.emit();
  }
  deleteButtonClicked() {
    this.delete.emit();
  }
}
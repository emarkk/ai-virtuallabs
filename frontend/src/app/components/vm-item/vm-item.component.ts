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
  baseLink: string;

  @Input() set data(value: Vm) {
    this.vm = value;
    this.owner = this.vm.owners.includes(this.authService.getId());
  }

  @Input() set connectLink(value: string) {
    this.baseLink = value;
  }

  @Output() toggleOnline = new EventEmitter<void>();
  @Output() addOwners = new EventEmitter<void>();
  @Output() delete = new EventEmitter<void>();

  constructor(private authService: AuthService) {
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
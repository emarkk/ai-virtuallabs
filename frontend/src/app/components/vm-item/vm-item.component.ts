import { Component, OnInit, Input } from '@angular/core';

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

  constructor() {
  }

  ngOnInit(): void {
  }

}
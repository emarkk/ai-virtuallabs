import { Component, OnInit, Input } from '@angular/core';

import { Vm } from 'src/app/core/models/vm.model';

@Component({
  selector: 'app-vm-list',
  templateUrl: './vm-list.component.html',
  styleUrls: ['./vm-list.component.css']
})
export class VmListComponent implements OnInit {
  vmList: Vm[] = null;

  @Input() set vms(data: Vm[]) {
    this.vmList = data;
  }

  constructor() {
  }

  ngOnInit(): void {
  }

}
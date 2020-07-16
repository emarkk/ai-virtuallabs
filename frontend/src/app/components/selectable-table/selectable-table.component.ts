import { Component, OnInit, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { merge } from 'rxjs';
import { tap } from 'rxjs/operators';

import { PagingSortingDataSource } from 'src/app/core/datasources/pagingsorting.datasource';

@Component({
  selector: 'app-selectable-table',
  templateUrl: './selectable-table.component.html',
  styleUrls: ['./selectable-table.component.css']
})
export class SelectableTableComponent implements OnInit {
  tableDataSource: PagingSortingDataSource<any>;
  tableColumns: any[] = null;

  columnsToDisplay: string[] = null;

  checkedSet = new Set<string>();
  masterChecked: boolean = false;
  masterSemichecked: boolean = false;
  allDatasetSelected: boolean = false;

  @ViewChild(MatPaginator)
  paginator: MatPaginator;

  @ViewChild(MatSort)
  sort: MatSort;

  @Input() set dataSource(value: PagingSortingDataSource<any>) {
    this.tableDataSource = value;
  }
  @Input() set columns(value: any[]) {
    this.tableColumns = value;
    this.columnsToDisplay = ['_select', ...value.map(c => c.name)];
  }

  @Output() select = new EventEmitter<Set<string> | 'all'>();

  constructor() {
  }

  ngOnInit(): void {
    this.tableDataSource.loadData();
  }

  ngAfterViewInit() {
    this.sort.sortChange.subscribe(() => {
      this.cancelSelection();
      this.paginator.pageIndex = 0;
    });
    merge(this.sort.sortChange, this.paginator.page).pipe(
      tap(() => this.loadDataPage())
    ).subscribe();
  }

  loadDataPage() {
    this.tableDataSource.loadData(this.sort.active, this.sort.direction, this.paginator.pageIndex, this.paginator.pageSize);
  }
  refresh() {
    this.checkedSet = new Set();
    this.masterChecked = false;
    this.masterSemichecked = false;
    this.allDatasetSelected = false;
    this.select.emit(this.checkedSet);
    this.tableDataSource.loadData(this.sort.active, this.sort.direction, this.paginator.pageIndex, this.paginator.pageSize);
  }
  selectAll() {
    this.allDatasetSelected = true;
    this.select.emit('all');
  }
  cancelSelection() {
    this.checkedSet = new Set();
    this.masterChecked = false;
    this.allDatasetSelected = false;
    this.select.emit(this.checkedSet);
  }
  setMasterState(checked: boolean) {
    if(checked)
      this.checkedSet = new Set(this.tableDataSource.currentPage.map(x => x.id.toString()));
    else
      this.checkedSet = new Set();
      
    this.masterChecked = checked;
    this.masterSemichecked = false;
    this.allDatasetSelected = false;
    this.select.emit(this.checkedSet);
  }
  getCheckedState(id: string) {
    return this.checkedSet.has(id);
  }
  setCheckedState(id: string, checked: boolean) {
    checked ? this.checkedSet.add(id) : this.checkedSet.delete(id);

    const currentPage = this.tableDataSource.currentPage;
    const checkedCount = currentPage.map(x => x.id.toString()).filter(x => this.checkedSet.has(x)).length;
    this.masterChecked = checkedCount == currentPage.length;
    this.masterSemichecked = checkedCount > 0 && !this.masterChecked;
    this.allDatasetSelected = false;

    this.select.emit(this.checkedSet);
  }
}
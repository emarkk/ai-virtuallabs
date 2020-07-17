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
  // data source for table
  tableDataSource: PagingSortingDataSource<any>;
  // list of table columns
  tableColumns: any[] = null;

  columnsToDisplay: string[] = null;

  // set of ids currently selected
  checkedSet = new Set<string>();
  // whether master checkbox is checked
  masterChecked: boolean = false;
  // whether master checkbox is semichecked
  masterSemichecked: boolean = false;
  // whether all dataset was selected
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
    // columns to display = _select + names of input columns
    this.columnsToDisplay = ['_select', ...value.map(c => c.name)];
  }

  // emits current selection to parent (can be a set of ids or the string 'all')
  @Output() select = new EventEmitter<Set<string> | 'all'>();

  constructor() {
  }

  ngOnInit(): void {
    // load data on init
    this.tableDataSource.loadData();
  }

  ngAfterViewInit() {
    // reset page number and selection when sorting changes
    this.sort.sortChange.subscribe(() => {
      this.cancelSelection();
      this.paginator.pageIndex = 0;
    });
    // react to changes in table sorting and paging settings
    merge(this.sort.sortChange, this.paginator.page).pipe(
      tap(() => this.loadDataPage())
    ).subscribe();
  }

  loadDataPage() {
    // load data page based on current sorting and paging settings
    this.tableDataSource.loadData(this.sort.active, this.sort.direction, this.paginator.pageIndex, this.paginator.pageSize);
  }
  refresh() {
    // data may have changed, so reset selection and fetch data again
    this.cancelSelection();
    this.loadDataPage();
  }
  selectAll() {
    this.allDatasetSelected = true;
    this.select.emit('all');
  }
  cancelSelection() {
    this.checkedSet = new Set();
    this.masterChecked = false;
    this.masterSemichecked = false;
    this.allDatasetSelected = false;
    this.select.emit(this.checkedSet);
  }
  setMasterState(checked: boolean) {
    // populate checked set appropriately
    if(checked)
      this.checkedSet = new Set(this.tableDataSource.currentPage.map(x => x.id.toString()));
    else
      this.checkedSet = new Set();
    
    // set master check state
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

    // recalculate master check state
    const currentPage = this.tableDataSource.currentPage;
    const checkedCount = currentPage.map(x => x.id.toString()).filter(x => this.checkedSet.has(x)).length;
    this.masterChecked = checkedCount == currentPage.length;
    this.masterSemichecked = checkedCount > 0 && !this.masterChecked;
    this.allDatasetSelected = false;

    this.select.emit(this.checkedSet);
  }
}
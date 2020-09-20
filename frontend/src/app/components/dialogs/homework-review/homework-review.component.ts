import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { APIResult } from 'src/app/core/models/api-result.model';

import { HomeworkService } from 'src/app/core/services/homework.service';

import { numberValidator } from 'src/app/core/validators/core.validator';

export interface HomeworkReviewDialogData {
  homeworkId: number;
  actionId: number;
}

@Component({
  selector: 'app-dialog-homework-review',
  templateUrl: './homework-review.component.html',
  styleUrls: ['./homework-review.component.css']
})
export class HomeworkReviewDialog implements OnInit {
  // whether it is possible to edit the form or not
  locked: boolean = false;
  // form fields for homework review
  form = new FormGroup({
    file: new FormControl({ value: null, disabled: false }, [Validators.required]),
    final: new FormControl({ value: false, disabled: false }, [Validators.required]),
    mark: new FormControl({ value: '', disabled: true }, [numberValidator, Validators.min(1), Validators.max(30)]),
  });
  
  constructor(public dialogRef: MatDialogRef<HomeworkReviewDialog>, @Inject(MAT_DIALOG_DATA) public data: HomeworkReviewDialogData, private homeworkService: HomeworkService) {}

  ngOnInit(): void {
    // 'mark' field should be enabled or not based on 'final' field
    this.form.get('final').valueChanges.subscribe(final => {
      if(final)
        this.form.get('mark').enable();
      else {
        this.form.get('mark').setValue('');
        this.form.get('mark').disable();
      }
    })
  }

  getFileErrorMessage() {
    if(this.form.get('file').hasError('required'))
      return 'You must upload the review file';
  }
  getMarkErrorMessage() {
    if(this.form.get('mark').disabled || !this.form.get('final').value)
      return '';

    if(this.form.get('mark').hasError('number'))
      return 'Please enter a number here';
    return this.form.get('mark').hasError('min') || this.form.get('mark').hasError('max') ? 'Marks are in the range 1-30' : '';
  }
  lock() {
    this.locked = true;
    this.form.disable();
  }
  unlock() {
    this.locked = false;
    this.form.enable();
  }
  saveButtonClicked() {
    // if form is invalid or locked, ignore
    if(this.form.invalid || this.locked)
      return;
      
    // collect form data
    const file = this.form.get('file').value._files[0];
    const mark = this.form.get('mark').value || null;

    // lock until request is completed
    this.lock();
    // post review attempt
    this.homeworkService.postReview(this.data.homeworkId, this.data.actionId, file, mark).subscribe((res: APIResult) => {
      // unlock form
      this.unlock();
      // return result to parent
      this.dialogRef.close(res);
    });
  }
}
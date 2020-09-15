import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { SafeUrl } from '@angular/platform-browser';

export interface ImageDialogData {
  imageUrl: SafeUrl
}

@Component({
  selector: 'app-dialog-image',
  templateUrl: './image.component.html',
  styleUrls: ['./image.component.css']
})
export class ImageDialog implements OnInit {
  
  constructor(public dialogRef: MatDialogRef<ImageDialog>, @Inject(MAT_DIALOG_DATA) public data: ImageDialogData) {}

  ngOnInit(): void {
  }

}
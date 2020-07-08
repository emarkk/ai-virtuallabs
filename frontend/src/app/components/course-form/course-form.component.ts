import { Component, OnInit, Input } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-course-form',
  templateUrl: './course-form.component.html',
  styleUrls: ['./course-form.component.css']
})
export class CourseFormComponent implements OnInit {
  locked: boolean = false;
  form = new FormGroup({
    firstName: new FormControl({ value: '', disabled: false }, [Validators.required]),
    lastName: new FormControl({ value: '', disabled: false }, [Validators.required]),
    matricola: new FormControl({ value: '', disabled: false }, [Validators.required]),
    email: new FormControl({ value: '', disabled: false }, [Validators.required]),
    password: new FormControl({ value: '', disabled: false }, [Validators.required, Validators.minLength(6)])
  });

  constructor() {
  }

  ngOnInit(): void {
  }

  saveButtonClicked() {
    
  }

}
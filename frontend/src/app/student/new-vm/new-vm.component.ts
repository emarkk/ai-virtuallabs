import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Observable } from 'rxjs';

import { Course } from 'src/app/core/models/course.model';

import { CourseService } from 'src/app/core/services/course.service';

import { navHome, navCourses, nav } from '../student.navdata';
import { VmModel } from 'src/app/core/models/vmmodel.model';

@Component({
  selector: 'app-student-new-vm',
  templateUrl: './new-vm.component.html',
  styleUrls: ['./new-vm.component.css']
})
export class StudentNewVmComponent implements OnInit {
  courseCode: string;
  courseName: string;
  course$: Observable<Course>;
  vmModel: VmModel;
  navigationData: Array<any>|null = null;

  locked: boolean = false;
  form = new FormGroup({
    model: new FormControl({ value: '', disabled: true }, [Validators.required]),
    vcpu: new FormControl({ value: '', disabled: false }, [Validators.required]),
    disk: new FormControl({ value: '', disabled: false }, [Validators.required]),
    ram: new FormControl({ value: '', disabled: false }, [Validators.required]),
  });

  constructor(private route: ActivatedRoute, private courseService: CourseService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseCode = params.code;
      this.course$ = this.courseService.get(this.courseCode);
      
      this.courseService.getVmModel(this.courseCode).subscribe(model => {
        this.vmModel = model;
        this.form.get('model').setValue(model.name);
      });

      this.course$.subscribe(course => {
        this.courseName = course.name;
        this.navigationData = [navHome, navCourses, nav(course.name, `/student/course/${course.code}`), nav('VMs', `/student/course/${course.code}/vms`), nav('New')];
      });
    });
  }

  lock() {
    this.locked = true;
    this.form.disable();
  }
  unlock() {
    this.locked = false;
    this.form.enable();
  }
  getVcpuErrorMessage() {
    if(this.form.get('vcpu').hasError('required'))
      return 'You must enter the number of virtual CPUs';
  }
  getDiskErrorMessage() {
    if(this.form.get('disk').hasError('required'))
      return 'You must enter the amount of disk space';
  }
  getRamErrorMessage() {
    if(this.form.get('ram').hasError('required'))
      return 'You must enter the amount of RAM';
  }
}

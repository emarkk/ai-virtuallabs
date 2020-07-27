import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Course } from 'src/app/core/models/course.model';
import { TeamStatus } from 'src/app/core/models/team.model';
import { VmModel } from 'src/app/core/models/vmmodel.model';

import { CourseService } from 'src/app/core/services/course.service';
import { VmService } from 'src/app/core/services/vm.service';
import { ToastService } from 'src/app/core/services/toast.service';
import { StudentService } from 'src/app/core/services/student.service';
import { AuthService } from 'src/app/core/services/auth.service';

import { numberValidator } from 'src/app/core/validators/core.validator';

import { navHome, navCourses, nav } from '../student.navdata';

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
  teamId: number;
  navigationData: Array<any>|null = null;

  locked: boolean = false;
  form = new FormGroup({
    model: new FormControl({ value: '', disabled: true }, [Validators.required]),
    vcpu: new FormControl({ value: '', disabled: false }, [Validators.required, numberValidator, Validators.min(1)]),
    disk: new FormControl({ value: '', disabled: false }, [Validators.required, numberValidator, Validators.min(1)]),
    ram: new FormControl({ value: '', disabled: false }, [Validators.required, numberValidator, Validators.min(1)]),
  });

  constructor(private route: ActivatedRoute, private router: Router, private authService: AuthService, private courseService: CourseService, private studentService: StudentService, private vmService: VmService, private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseCode = params.code;
      this.course$ = this.courseService.get(this.courseCode);
      
      this.courseService.getVmModel(this.courseCode).subscribe(model => {
        this.vmModel = model;
        this.form.get('model').setValue(model.name);
      });

      this.studentService.getTeamsForCourse(this.authService.getId(), this.courseCode).pipe(map(teams => teams.find(t => t.status == TeamStatus.COMPLETE))).subscribe(team => {
        if(team)
          this.teamId = team.id;
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
    if(this.form.get('vcpu').hasError('number') || this.form.get('vcpu').hasError('min'))
      return 'Please enter a positive number here';
  }
  getDiskErrorMessage() {
    if(this.form.get('disk').hasError('required'))
      return 'You must enter the amount of disk space';
    if(this.form.get('disk').hasError('number') || this.form.get('disk').hasError('min'))
      return 'Please enter a positive number here';
  }
  getRamErrorMessage() {
    if(this.form.get('ram').hasError('required'))
      return 'You must enter the amount of RAM';
    if(this.form.get('ram').hasError('number') || this.form.get('ram').hasError('min'))
      return 'Please enter a positive number here';
  }
  createButtonClicked() {
    if(this.form.invalid || this.locked)
      return;

    const vcpu = this.form.get('vcpu').value;
    const disk = this.form.get('disk').value;
    const ram = this.form.get('ram').value;
  
    this.lock();
    this.vmService.add(vcpu, disk, ram, this.teamId).subscribe(res => {
      this.unlock();
      if(res) {
        this.router.navigate([`/student/course/${this.courseCode}`]);
        this.toastService.show({ type: 'success', text: 'VM created successfully.' });
      } else
        this.toastService.show({ type: 'danger', text: 'An error occurred.' });
    })
  }
}

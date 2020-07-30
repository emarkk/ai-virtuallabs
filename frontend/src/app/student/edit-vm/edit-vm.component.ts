import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Course } from 'src/app/core/models/course.model';
import { TeamStatus } from 'src/app/core/models/team.model';
import { VmModel } from 'src/app/core/models/vmmodel.model';
import { Vm } from 'src/app/core/models/vm.model';

import { CourseService } from 'src/app/core/services/course.service';
import { VmService } from 'src/app/core/services/vm.service';
import { StudentService } from 'src/app/core/services/student.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { ToastService } from 'src/app/core/services/toast.service';

import { VmFormComponent } from 'src/app/components/vm-form/vm-form.component';

import { navHome, navCourses, nav } from '../student.navdata';

@Component({
  selector: 'app-student-edit-vm',
  templateUrl: './edit-vm.component.html',
  styleUrls: ['./edit-vm.component.css']
})
export class StudentEditVmComponent implements OnInit {
  courseCode: string;
  courseName: string;
  course$: Observable<Course>;

  vmId: number;
  vmModel: VmModel;
  vm$: Observable<Vm>;

  navigationData: Array<any>|null = null;

  @ViewChild(VmFormComponent)
  formComponent: VmFormComponent;

  constructor(private router: Router, private route: ActivatedRoute, private authService: AuthService, private courseService: CourseService, private studentService: StudentService, private vmService: VmService, private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseCode = params.code;
      this.course$ = this.courseService.get(this.courseCode);
      
      this.vmId = params.id;
      this.vm$ = this.vmService.get(this.vmId);
      
      this.courseService.getVmModel(this.courseCode).subscribe(model => {
        this.vmModel = model;
      });

      this.course$.subscribe(course => {
        this.courseName = course.name;
        this.navigationData = [navHome, navCourses, nav(course.name, `/student/course/${course.code}`), nav('VMs', `/student/course/${course.code}/vms`), nav('New')];
      });
    });
  }

  saveVm(vmData) {
    this.formComponent.lock();
    const { vcpu, disk, ram } = vmData;
    this.vmService.update(this.vmId, vcpu, disk, ram).subscribe(res => {
      this.formComponent.unlock();
      if(res) {
        this.router.navigate([`/student/course/${this.courseCode}`]);
        this.toastService.show({ type: 'success', text: 'VM information updated successfully.' });
      } else
        this.toastService.show({ type: 'danger', text: 'An error occurred.' });
    });
  }
}

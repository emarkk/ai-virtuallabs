import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
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

import { navHome, navCourses, nav } from '../student.navdata';
import { VmFormComponent } from 'src/app/components/vm-form/vm-form.component';

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

  @ViewChild(VmFormComponent)
  formComponent: VmFormComponent;

  constructor(private route: ActivatedRoute, private router: Router, private authService: AuthService, private courseService: CourseService, private studentService: StudentService, private vmService: VmService, private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseCode = params.code;
      this.course$ = this.courseService.get(this.courseCode);
      
      this.courseService.getVmModel(this.courseCode).subscribe(model => {
        this.vmModel = model;
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

  saveVm(vmData) {
    this.formComponent.lock();
    const { vcpu, disk, ram } = vmData;
    this.vmService.add(vcpu, disk, ram, this.teamId).subscribe(res => {
      this.formComponent.unlock();
      if(res) {
        this.router.navigate([`/student/course/${this.courseCode}`]);
        this.toastService.show({ type: 'success', text: 'VM created successfully.' });
      } else
        this.toastService.show({ type: 'danger', text: 'An error occurred.' });
    });
  }
}

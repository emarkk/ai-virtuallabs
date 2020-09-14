import { Role } from '../core/models/role.enum';
import { AuthGuard } from '../core/guards/auth.guard';

import { StudentContainerComponent } from './container/container.component';
import { StudentHomeComponent } from './home/home.component';
import { StudentCoursesComponent } from './courses/courses.component';
import { StudentCourseDetailComponent } from './course-detail/course-detail.component';
import { StudentNewTeamComponent } from './new-team/new-team.component';
import { StudentVmsComponent } from './vms/vms.component';
import { StudentNewVmComponent } from './new-vm/new-vm.component';
import { StudentVmComponent } from './vm/vm.component';
import { StudentEditVmComponent } from './edit-vm/edit-vm.component';
import { StudentHomeworksComponent } from './homeworks/homeworks.component';
import { StudentHomeworkDetailComponent } from './homework-detail/homework-detail.component';

export const studentRouting = [
  {
    path: 'student',
    component: StudentContainerComponent,
    canActivate: [AuthGuard],
    data: { roles: [Role.STUDENT] },
    children: [
      {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full'
      },
      {
        path: 'home',
        component: StudentHomeComponent
      },
      {
        path: 'courses',
        component: StudentCoursesComponent,
      },
      {
        path: 'course/:code',
        component: StudentCourseDetailComponent,
      },
      {
        path: 'course/:code/teams/new',
        component: StudentNewTeamComponent,
      },
      {
        path: 'course/:code/vms',
        component: StudentVmsComponent,
      },
      {
        path: 'course/:code/vms/new',
        component: StudentNewVmComponent,
      },
      {
        path: 'course/:code/vm/:id',
        component: StudentVmComponent,
      },
      {
        path: 'course/:code/vm/:id/edit',
        component: StudentEditVmComponent,
      },
      {
        path: 'course/:code/homeworks',
        component: StudentHomeworksComponent,
      },
      {
        path: 'course/:code/homework/:id',
        component: StudentHomeworkDetailComponent,
      }
    ]
  }
];
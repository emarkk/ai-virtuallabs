import { Role } from '../core/models/role.enum';
import { AuthGuard } from '../core/guards/auth.guard';

import { StudentContainerComponent } from './container/container.component';
import { StudentHomeComponent } from './home/home.component';
import { StudentCoursesComponent } from './courses/courses.component';
import { StudentCourseDetailComponent } from './course-detail/course-detail.component';
import { StudentNewTeamComponent } from './new-team/new-team.component';

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
        path: 'course/:code/team/new',
        component: StudentNewTeamComponent,
      }
    ]
  }
];
import { Role } from '../core/models/role.enum';
import { AuthGuard } from '../core/guards/auth.guard';

import { StudentContainerComponent } from './container/container.component';
import { StudentHomeComponent } from './home/home.component';

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
      }
    ]
  }
];
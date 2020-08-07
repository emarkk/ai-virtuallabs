import { Role } from '../core/models/role.enum';
import { AuthGuard } from '../core/guards/auth.guard';

import { ProfessorContainerComponent } from './container/container.component';
import { ProfessorHomeComponent } from './home/home.component';
import { ProfessorCoursesComponent } from './courses/courses.component';
import { ProfessorNewCourseComponent } from './new-course/new-course.component';
import { ProfessorEditCourseComponent } from './edit-course/edit-course.component';
import { ProfessorCourseDetailComponent } from './course-detail/course-detail.component';
import { ProfessorStudentsComponent } from './students/students.component';
import { ProfessorTeamsComponent } from './teams/teams.component';
import { ProfessorVmsComponent } from './vms/vms.component';
import { ProfessorVmComponent } from './vm/vm.component';
import { ProfessorHomeworksComponent } from './homeworks/homeworks.component';
import { ProfessorNewHomeworkComponent } from './new-homework/new-homework.component';

export const professorRouting = [
  {
    path: 'professor',
    component: ProfessorContainerComponent,
    canActivate: [AuthGuard], 
    data: { roles: [Role.PROFESSOR] },
    children: [
      {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full'
      },
      {
        path: 'home',
        component: ProfessorHomeComponent
      },
      {
        path: 'courses',
        component: ProfessorCoursesComponent,
      },
      {
        path: 'courses/new',
        component: ProfessorNewCourseComponent,
      },
      {
        path: 'course/:code/edit',
        component: ProfessorEditCourseComponent,
      },
      {
        path: 'course/:code',
        component: ProfessorCourseDetailComponent,
      },
      {
        path: 'course/:code/students',
        component: ProfessorStudentsComponent,
      },
      {
        path: 'course/:code/teams',
        component: ProfessorTeamsComponent,
      },
      {
        path: 'course/:code/vms',
        component: ProfessorVmsComponent,
      },
      {
        path: 'course/:code/vm/:id',
        component: ProfessorVmComponent,
      },
      {
        path: 'course/:code/homeworks',
        component: ProfessorHomeworksComponent,
      },
      {
        path: 'course/:code/homeworks/new',
        component: ProfessorNewHomeworkComponent,
      }
    ]
  }
];
import { Role } from '../core/models/role.enum';
import { AuthGuard } from '../core/guards/auth.guard';

import { ProfessorContainerComponent } from './container/container.component';
import { ProfessorHomeComponent } from './home/home.component';
import { ProfessorCoursesComponent } from './courses/courses.component';
import { ProfessorNewCourseComponent } from './new-course/new-course.component';
import { ProfessorEditCourseComponent } from './edit-course/edit-course.component';
import { ProfessorCourseDetailComponent } from './course-detail/course-detail.component';
import { ProfessorCourseStudentsComponent } from './course-students/course-students.component';
import { ProfessorCourseTeamsComponent } from './course-teams/course-teams.component';
import { ProfessorCourseHomeworksComponent } from './course-homeworks/course-homeworks.component';

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
        component: ProfessorCourseStudentsComponent,
      },
      {
        path: 'course/:code/teams',
        component: ProfessorCourseTeamsComponent,
      },
      {
        path: 'course/:code/homeworks',
        component: ProfessorCourseHomeworksComponent,
      }
    ]
  }
];
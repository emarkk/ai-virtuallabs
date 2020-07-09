import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { professorRouting } from './professor/professor.routing';
import { studentRouting } from './student/student.routing';

import { SignInComponent } from './auth/signin/signin.component';
import { SignUpComponent } from './auth/signup/signup.component';
import { SignUpSuccessComponent } from './auth/signup-success/signup-success.component';
import { ConfirmComponent } from './auth/confirm/confirm.component';
import { ContainerComponent } from './container/container.component';
import { HomeComponent } from './home/home.component';

const routes: Routes = [
  {
    path: 'signin',
    component: SignInComponent
  },
  {
    path: 'signup',
    component: SignUpComponent
  },
  {
    path: 'signup/success',
    component: SignUpSuccessComponent
  },
  {
    path: 'signup/confirm/:token',
    component: ConfirmComponent
  },
  {
    path: '',
    component: ContainerComponent,
    children: [
      {
        path: '',
        component: HomeComponent
      },
      ...professorRouting,
      ...studentRouting
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

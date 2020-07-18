import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AuthInterceptor } from './core/interceptors/auth.interceptor';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatDialogModule } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatChipsModule } from '@angular/material/chips';

import { AppComponent } from './app.component';
import { PageComponent } from './components/page/page.component';
import { ConfirmDialog } from './components/dialogs/confirm/confirm.component';
import { TileComponent } from './components/tile/tile.component';
import { ToastComponent } from './components/toast/toast.component';
import { AsyncButtonComponent } from './components/async-button/async-button.component';
import { UserTagComponent } from './components/user-tag/user-tag.component';
import { NavigationComponent } from './components/navigation/navigation.component';
import { DataPlaceholderComponent } from './components/data-placeholder/data-placeholder.component';
import { FullscreenSearchComponent } from './components/fullscreen-search/fullscreen-search.component';
import { SelectableTableComponent } from './components/selectable-table/selectable-table.component';
import { WelcomeComponent } from './components/welcome/welcome.component';
import { CourseItemComponent } from './components/course-item/course-item.component';
import { CourseListComponent } from './components/course-list/course-list.component';
import { CourseFormComponent } from './components/course-form/course-form.component';

import { ContainerComponent } from './container/container.component';
import { SignInComponent } from './auth/signin/signin.component';
import { SignUpComponent } from './auth/signup/signup.component';
import { SignUpSuccessComponent } from './auth/signup-success/signup-success.component';
import { ConfirmComponent } from './auth/confirm/confirm.component';
import { HomeComponent } from './home/home.component';
import { ProfessorContainerComponent } from './professor/container/container.component';
import { ProfessorHomeComponent } from './professor/home/home.component';
import { ProfessorCoursesComponent } from './professor/courses/courses.component';
import { ProfessorNewCourseComponent } from './professor/new-course/new-course.component';
import { ProfessorEditCourseComponent } from './professor/edit-course/edit-course.component';
import { ProfessorCourseDetailComponent } from './professor/course-detail/course-detail.component';
import { ProfessorCourseStudentsComponent } from './professor/course-students/course-students.component';
import { ProfessorCourseTeamsComponent } from './professor/course-teams/course-teams.component';
import { ProfessorCourseHomeworksComponent } from './professor/course-homeworks/course-homeworks.component';
import { StudentContainerComponent } from './student/container/container.component';
import { StudentHomeComponent } from './student/home/home.component';
import { StudentCoursesComponent } from './student/courses/courses.component';
import { StudentCourseDetailComponent } from './student/course-detail/course-detail.component';
import { StudentNewTeamComponent } from './student/new-team/new-team.component';

@NgModule({
  declarations: [
    AppComponent,
    PageComponent,
    ConfirmDialog,
    TileComponent,
    ToastComponent,
    AsyncButtonComponent,
    UserTagComponent,
    WelcomeComponent,
    NavigationComponent,
    DataPlaceholderComponent,
    FullscreenSearchComponent,
    SelectableTableComponent,
    CourseItemComponent,
    CourseListComponent,
    CourseFormComponent,
    ContainerComponent,
    SignInComponent,
    SignUpComponent,
    SignUpSuccessComponent,
    ConfirmComponent,
    HomeComponent,
    ProfessorContainerComponent,
    ProfessorHomeComponent,
    ProfessorCoursesComponent,
    ProfessorNewCourseComponent,
    ProfessorEditCourseComponent,
    ProfessorCourseDetailComponent,
    ProfessorCourseStudentsComponent,
    ProfessorCourseTeamsComponent,
    ProfessorCourseHomeworksComponent,
    StudentContainerComponent,
    StudentHomeComponent,
    StudentCoursesComponent,
    StudentCourseDetailComponent,
    StudentNewTeamComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatTabsModule,
    MatTableModule,
    MatCheckboxModule,
    MatButtonModule,
    MatInputModule,
    MatAutocompleteModule,
    MatSortModule,
    MatPaginatorModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    MatSlideToggleModule,
    MatChipsModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

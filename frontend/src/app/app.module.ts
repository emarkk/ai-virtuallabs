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

import { AppComponent } from './app.component';
import { PageComponent } from './components/page/page.component';
import { TileComponent } from './components/tile/tile.component';
import { AsyncButtonComponent } from './components/async-button/async-button.component';
import { NavigationComponent } from './components/navigation/navigation.component';
import { DataPlaceholderComponent } from './components/data-placeholder/data-placeholder.component';
import { WelcomeComponent } from './components/welcome/welcome.component';
import { CourseItemComponent } from './components/course-item/course-item.component';
import { CourseListComponent } from './components/course-list/course-list.component';
import { CourseFormComponent } from './components/course-form/course-form.component';

import { ContainerComponent } from './container/container.component';
import { SignInComponent } from './auth/signin/signin.component';
import { SignUpComponent } from './auth/signup/signup.component';
import { SignUpSuccessComponent } from './auth/signup-success/signup-success.component';
import { HomeComponent } from './home/home.component';
import { ProfessorContainerComponent } from './professor/container/container.component';
import { ProfessorHomeComponent } from './professor/home/home.component';
import { ProfessorCoursesComponent } from './professor/courses/courses.component';
import { ProfessorCourseDetailComponent } from './professor/course-detail/course-detail.component';
import { ProfessorNewCourseComponent } from './professor/new-course/new-course.component';
import { ProfessorEditCourseComponent } from './professor/edit-course/edit-course.component';
import { StudentContainerComponent } from './student/container/container.component';
import { StudentHomeComponent } from './student/home/home.component';

@NgModule({
  declarations: [
    AppComponent,
    PageComponent,
    TileComponent,
    AsyncButtonComponent,
    WelcomeComponent,
    NavigationComponent,
    DataPlaceholderComponent,
    CourseItemComponent,
    CourseListComponent,
    CourseFormComponent,
    ContainerComponent,
    SignInComponent,
    SignUpComponent,
    SignUpSuccessComponent,
    HomeComponent,
    ProfessorContainerComponent,
    ProfessorHomeComponent,
    ProfessorCoursesComponent,
    ProfessorNewCourseComponent,
    ProfessorCourseDetailComponent,
    ProfessorEditCourseComponent,
    StudentContainerComponent,
    StudentHomeComponent
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
    MatSlideToggleModule
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

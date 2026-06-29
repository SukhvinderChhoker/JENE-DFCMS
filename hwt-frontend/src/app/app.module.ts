import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthInterceptor } from './interceptors/auth.interceptor';

import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { CaseListComponent } from './components/cases/case-list/case-list.component';
import { CaseDetailComponent } from './components/cases/case-detail/case-detail.component';
import { CaseFormComponent } from './components/cases/case-form/case-form.component';
import { TaskListComponent } from './components/tasks/task-list/task-list.component';
import { TaskDetailComponent } from './components/tasks/task-detail/task-detail.component';
import { TaskFormComponent } from './components/tasks/task-form/task-form.component';
import { EvidenceListComponent } from './components/evidence/evidence-list/evidence-list.component';
import { EvidenceDetailComponent } from './components/evidence/evidence-detail/evidence-detail.component';
import { EvidenceFormComponent } from './components/evidence/evidence-form/evidence-form.component';
import { UserListComponent } from './components/users/user-list/user-list.component';
import { UserFormComponent } from './components/users/user-form/user-form.component';
import { NavbarComponent } from './components/shared/navbar/navbar.component';
import { SidebarComponent } from './components/shared/sidebar/sidebar.component';
import { SearchComponent } from './components/search/search.component';
import { QuizComponent } from './components/quiz/quiz.component';
import { QuizResultComponent } from './components/quiz/quiz-result.component';
import { FinalReportsComponent } from './components/final-reports/final-reports.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    DashboardComponent,
    CaseListComponent,
    CaseDetailComponent,
    CaseFormComponent,
    TaskListComponent,
    TaskDetailComponent,
    TaskFormComponent,
    EvidenceListComponent,
    EvidenceDetailComponent,
    EvidenceFormComponent,
    UserListComponent,
    UserFormComponent,
    NavbarComponent,
    SidebarComponent,
    SearchComponent,
    QuizComponent,
    QuizResultComponent,
    FinalReportsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}

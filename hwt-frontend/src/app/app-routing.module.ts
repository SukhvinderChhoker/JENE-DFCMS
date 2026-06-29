import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { CaseListComponent } from './components/cases/case-list/case-list.component';
import { CaseDetailComponent } from './components/cases/case-detail/case-detail.component';
import { CaseFormComponent } from './components/cases/case-form/case-form.component';
import { TaskDetailComponent } from './components/tasks/task-detail/task-detail.component';
import { TaskFormComponent } from './components/tasks/task-form/task-form.component';
import { EvidenceListComponent } from './components/evidence/evidence-list/evidence-list.component';
import { EvidenceDetailComponent } from './components/evidence/evidence-detail/evidence-detail.component';
import { EvidenceFormComponent } from './components/evidence/evidence-form/evidence-form.component';
import { UserListComponent } from './components/users/user-list/user-list.component';
import { UserFormComponent } from './components/users/user-form/user-form.component';
import { SearchComponent } from './components/search/search.component';
import { QuizComponent } from './components/quiz/quiz.component';
import { QuizResultComponent } from './components/quiz/quiz-result.component';

const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'search', component: SearchComponent, canActivate: [AuthGuard] },
  { path: 'quiz', component: QuizComponent, canActivate: [AuthGuard] },
  { path: 'quiz/result', component: QuizResultComponent, canActivate: [AuthGuard] },
  { path: 'cases', component: CaseListComponent, canActivate: [AuthGuard] },
  { path: 'cases/new', component: CaseFormComponent, canActivate: [AuthGuard] },
  { path: 'cases/:id', component: CaseDetailComponent, canActivate: [AuthGuard] },
  { path: 'cases/:id/edit', component: CaseFormComponent, canActivate: [AuthGuard] },
  { path: 'cases/:id/tasks/new', component: TaskFormComponent, canActivate: [AuthGuard] },
  { path: 'tasks/:id', component: TaskDetailComponent, canActivate: [AuthGuard] },
  { path: 'tasks/:id/edit', component: TaskFormComponent, canActivate: [AuthGuard] },
  { path: 'evidence', component: EvidenceListComponent, canActivate: [AuthGuard] },
  { path: 'evidence/new', component: EvidenceFormComponent, canActivate: [AuthGuard] },
  { path: 'evidence/:id', component: EvidenceDetailComponent, canActivate: [AuthGuard] },
  { path: 'evidence/:id/edit', component: EvidenceFormComponent, canActivate: [AuthGuard] },
  { path: 'users', component: UserListComponent, canActivate: [AuthGuard] },
  { path: 'users/new', component: UserFormComponent, canActivate: [AuthGuard] },
  { path: 'users/:id/edit', component: UserFormComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: '/dashboard' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}

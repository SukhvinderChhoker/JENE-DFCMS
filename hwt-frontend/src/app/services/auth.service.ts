import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { LoginRequest, LoginResponse, User } from '../models/user.model';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api';
  private currentUserSubject = new BehaviorSubject<LoginResponse | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    const stored = localStorage.getItem('currentUser');
    if (stored) {
      this.currentUserSubject.next(JSON.parse(stored));
    }
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, credentials).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('currentUser', JSON.stringify(response));
        this.currentUserSubject.next(response);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getCurrentUser(): LoginResponse | null {
    return this.currentUserSubject.value;
  }

  getRoles(): string[] {
    const user = this.getCurrentUser();
    return user ? user.roles : [];
  }

  isAdmin(): boolean {
    return this.getRoles().includes('ADMIN');
  }

  isCaseManager(): boolean {
    return this.getRoles().includes('CASE_MANAGER');
  }

  isInvestigator(): boolean {
    return this.getRoles().includes('INVESTIGATOR');
  }

  isQA(): boolean {
    return this.getRoles().includes('QA');
  }

  isRequester(): boolean {
    return this.getRoles().includes('REQUESTER');
  }

  isAuthoriser(): boolean {
    return this.getRoles().includes('AUTHORISER');
  }

  getSelectedRole(): string {
    return localStorage.getItem('selectedRole') || '';
  }

  hasSelectedRole(role: string): boolean {
    return this.getSelectedRole() === role;
  }

  canManageCases(): boolean {
    const r = this.getSelectedRole();
    return r === 'ADMIN' || r === 'CASE_MANAGER';
  }

  canCreateCases(): boolean {
    const r = this.getSelectedRole();
    return r === 'ADMIN' || r === 'CASE_MANAGER' || r === 'REQUESTER';
  }

  canAuthorizeCases(): boolean {
    const r = this.getSelectedRole();
    return r === 'ADMIN' || r === 'AUTHORISER';
  }

  canManageTasks(): boolean {
    const r = this.getSelectedRole();
    return r === 'ADMIN' || r === 'CASE_MANAGER';
  }

  canWorkOnTasks(): boolean {
    const r = this.getSelectedRole();
    return r === 'ADMIN' || r === 'CASE_MANAGER' || r === 'INVESTIGATOR' || r === 'QA';
  }

  canManageEvidence(): boolean {
    const r = this.getSelectedRole();
    return r === 'ADMIN' || r === 'CASE_MANAGER' || r === 'INVESTIGATOR';
  }

  canManageUsers(): boolean {
    return this.getSelectedRole() === 'ADMIN';
  }
}

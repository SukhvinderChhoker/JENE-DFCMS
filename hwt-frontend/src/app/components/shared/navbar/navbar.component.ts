import { Component } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  userMenuOpen = false;

  constructor(public authService: AuthService, private router: Router) {}

  toggleUserMenu(): void {
    this.userMenuOpen = !this.userMenuOpen;
  }

  logout(): void {
    this.authService.logout();
  }

  navigate(path: string): void {
    this.router.navigate([path]);
    this.userMenuOpen = false;
  }

  getRoleLabel(): string {
    const role = this.authService.getSelectedRole();
    const labels: Record<string, string> = {
      'ADMIN': 'Admin',
      'CASE_MANAGER': 'Case Manager',
      'INVESTIGATOR': 'Investigator',
      'QA': 'QA',
      'REQUESTER': 'Requester',
      'AUTHORISER': 'Authoriser'
    };
    return labels[role] || role;
  }

  getRoleBadgeClass(): string {
    const role = this.authService.getSelectedRole();
    const classes: Record<string, string> = {
      'ADMIN': 'badge-admin',
      'CASE_MANAGER': 'badge-cm',
      'INVESTIGATOR': 'badge-inv',
      'QA': 'badge-qa',
      'REQUESTER': 'badge-req',
      'AUTHORISER': 'badge-auth'
    };
    return classes[role] || '';
  }
}

import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username = '';
  password = '';
  selectedRole = '';
  errorMessage = '';
  loading = false;

  constructor(private authService: AuthService, private router: Router) {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/dashboard']);
    }
  }

  onSubmit(): void {
    this.errorMessage = '';
    if (!this.selectedRole) {
      this.errorMessage = 'Please select a role to login';
      return;
    }
    this.loading = true;
    this.authService.login({ username: this.username, password: this.password }).subscribe({
      next: (response) => {
        const userRoles = response.roles || [];
        if (!userRoles.includes(this.selectedRole)) {
          this.loading = false;
          this.errorMessage = `You do not have the ${this.selectedRole} role. Your roles: ${userRoles.join(', ')}`;
          this.authService.logout();
          return;
        }
        localStorage.setItem('selectedRole', this.selectedRole);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Invalid username or password';
      }
    });
  }
}

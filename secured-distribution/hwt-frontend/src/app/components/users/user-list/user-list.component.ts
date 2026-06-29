import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../services/user.service';
import { User } from '../../../models/user.model';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {
  users: User[] = [];
  loading = true;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.getAll().subscribe({
      next: (data) => {
        this.users = data;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  getRoleBadgeClass(role: string): string {
    const classes: { [key: string]: string } = {
      'ADMIN': 'role-admin',
      'CASE_MANAGER': 'role-cm',
      'INVESTIGATOR': 'role-inv',
      'QA': 'role-qa',
      'REQUESTER': 'role-req',
      'AUTHORISER': 'role-auth'
    };
    return classes[role] || 'role-default';
  }
}

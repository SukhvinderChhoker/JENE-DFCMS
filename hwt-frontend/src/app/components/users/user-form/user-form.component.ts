import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../../services/user.service';
import { User } from '../../../models/user.model';

@Component({
  selector: 'app-user-form',
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.css']
})
export class UserFormComponent implements OnInit {
  form: FormGroup;
  isEdit = false;
  userId: number = 0;
  submitting = false;

  allRoles = ['ADMIN', 'CASE_MANAGER', 'INVESTIGATOR', 'QA', 'REQUESTER', 'AUTHORISER'];
  teams = ['Digital Forensics', 'Cybercrime', 'Intelligence', 'Operations', 'Management'];
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService
  ) {
    this.form = this.fb.group({
      username: ['', Validators.required],
      password: ['', this.isEdit ? [] : [Validators.required, Validators.minLength(8)]],
      forename: ['', Validators.required],
      surname: ['', Validators.required],
      middleName: [''],
      email: ['', [Validators.required, Validators.email]],
      telephone: [''],
      jobTitle: [''],
      team: [''],
      active: [true],
      roles: [[]]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.userId = Number(id);
      this.form.get('password')?.clearValidators();
      this.form.get('password')?.updateValueAndValidity();
      this.loadUser();
    }
  }

  loadUser(): void {
    this.userService.getById(this.userId).subscribe({
      next: (data) => {
        this.form.patchValue({
          username: data.username,
          forename: data.forename,
          surname: data.surname,
          middleName: data.middleName,
          email: data.email,
          telephone: data.telephone,
          jobTitle: data.jobTitle,
          team: data.team,
          active: data.active,
          roles: data.roles || []
        });
      }
    });
  }

  onRoleChange(role: string, event: any): void {
    const roles = this.form.get('roles')?.value || [];
    if (event.target.checked) {
      roles.push(role);
    } else {
      const index = roles.indexOf(role);
      if (index > -1) roles.splice(index, 1);
    }
    this.form.patchValue({ roles });
  }

  isRoleChecked(role: string): boolean {
    const roles = this.form.get('roles')?.value || [];
    return roles.includes(role);
  }

  onSubmit(): void {
    this.errorMessage = '';
    if (this.form.valid) {
      this.submitting = true;
      const data = { ...this.form.value };

      if (this.isEdit && !data.password) {
        delete data.password;
      }

      if (this.isEdit) {
        this.userService.update(this.userId, data).subscribe({
          next: () => this.router.navigate(['/users']),
          error: (err) => {
            this.errorMessage = err.error?.message || 'Failed to update user';
            this.submitting = false;
          }
        });
      } else {
        this.userService.create(data).subscribe({
          next: () => this.router.navigate(['/users']),
          error: (err) => {
            this.errorMessage = err.error?.message || 'Failed to create user';
            this.submitting = false;
          }
        });
      }
    }
  }
}

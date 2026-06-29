import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TaskService } from '../../../services/task.service';
import { UserService } from '../../../services/user.service';
import { User } from '../../../models/user.model';

@Component({
  selector: 'app-task-form',
  templateUrl: './task-form.component.html',
  styleUrls: ['./task-form.component.css']
})
export class TaskFormComponent implements OnInit {
  form: FormGroup;
  isEdit = false;
  taskId: number = 0;
  caseId: number = 0;
  users: User[] = [];
  submitting = false;

  taskTypes = ['Examination', 'Analysis', 'Research', 'Interview', 'Other'];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private taskService: TaskService,
    private userService: UserService
  ) {
    this.form = this.fb.group({
      taskName: ['', Validators.required],
      taskType: ['Examination', Validators.required],
      background: [''],
      location: [''],
      deadline: ['']
    });
  }

  ngOnInit(): void {
    this.loadUsers();
    const caseId = this.route.snapshot.paramMap.get('id');
    if (caseId) {
      this.caseId = Number(caseId);
    }

    const taskId = this.route.snapshot.paramMap.get('id');
    if (this.router.url.includes('/tasks/') && this.router.url.includes('/edit')) {
      this.isEdit = true;
      this.taskId = Number(this.route.snapshot.paramMap.get('id'));
      this.loadTask();
    }
  }

  loadUsers(): void {
    this.userService.getAll().subscribe({
      next: (data) => this.users = data
    });
  }

  loadTask(): void {
    this.taskService.getById(this.taskId).subscribe({
      next: (data) => {
        this.caseId = data.caseId;
        this.form.patchValue({
          taskName: data.taskName,
          taskType: data.taskType,
          background: data.background,
          location: data.location,
          deadline: data.deadline
        });
      }
    });
  }

  onSubmit(): void {
    if (this.form.valid) {
      this.submitting = true;
      const data = this.form.value;

      if (this.isEdit) {
        this.taskService.update(this.taskId, data).subscribe({
          next: () => this.router.navigate(['/tasks', this.taskId]),
          error: () => this.submitting = false
        });
      } else {
        this.taskService.create(this.caseId, data).subscribe({
          next: (created) => this.router.navigate(['/tasks', created.id]),
          error: () => this.submitting = false
        });
      }
    }
  }
}

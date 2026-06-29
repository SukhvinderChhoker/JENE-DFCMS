import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TaskService } from '../../../services/task.service';
import { UserService } from '../../../services/user.service';
import { Task, TaskNote } from '../../../models/task.model';
import { User } from '../../../models/user.model';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-task-detail',
  templateUrl: './task-detail.component.html',
  styleUrls: ['./task-detail.component.css']
})
export class TaskDetailComponent implements OnInit {
  task: Task | null = null;
  notes: TaskNote[] = [];
  users: User[] = [];
  newNote = '';
  loading = true;
  showAssignModal = false;
  assignType = '';
  selectedUserId = 0;
  isPrinciple = true;
  showStatusModal = false;
  statusForm = { status: '', note: '' };

  statusActions = [
    { status: 'IN_PROGRESS', label: 'Start Work', icon: 'play_arrow', color: '#4caf50' },
    { status: 'PENDING_QA', label: 'Request QA', icon: 'rate_review', color: '#ff9800' },
    { status: 'QA_PASSED', label: 'Pass QA', icon: 'check_circle', color: '#4caf50' },
    { status: 'QA_FAILED', label: 'Fail QA', icon: 'cancel', color: '#f44336' },
    { status: 'COMPLETED', label: 'Complete', icon: 'done_all', color: '#1a237e' },
    { status: 'CLOSED', label: 'Close', icon: 'lock', color: '#9e9e9e' }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private taskService: TaskService,
    private userService: UserService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.loadTask(id);
      this.loadNotes(id);
      this.loadUsers();
    }
  }

  loadTask(id: number): void {
    this.taskService.getById(id).subscribe({
      next: (data) => {
        this.task = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.router.navigate(['/cases']);
      }
    });
  }

  loadNotes(id: number): void {
    this.taskService.getNotes(id).subscribe({
      next: (data) => this.notes = data
    });
  }

  loadUsers(): void {
    this.userService.getAll().subscribe({
      next: (data) => this.users = data
    });
  }

  addNote(): void {
    if (this.newNote.trim() && this.task) {
      this.taskService.addNote(this.task.id, this.newNote).subscribe({
        next: (note) => {
          this.notes.unshift(note);
          this.newNote = '';
        }
      });
    }
  }

  changeStatus(status: string): void {
    this.statusForm.status = status;
    this.statusForm.note = '';
    this.showStatusModal = true;
  }

  confirmStatusChange(): void {
    if (this.task) {
      this.taskService.changeStatus(this.task.id, this.statusForm.status, this.statusForm.note).subscribe({
        next: () => {
          this.loadTask(this.task!.id);
          this.showStatusModal = false;
        }
      });
    }
  }

  openAssignModal(type: string): void {
    this.assignType = type;
    this.selectedUserId = 0;
    this.isPrinciple = true;
    this.showAssignModal = true;
  }

  confirmAssign(): void {
    if (this.task && this.selectedUserId) {
      if (this.assignType === 'investigator') {
        this.taskService.assignInvestigator(this.task.id, this.selectedUserId, this.isPrinciple).subscribe({
          next: () => {
            this.loadTask(this.task!.id);
            this.showAssignModal = false;
          }
        });
      } else {
        this.taskService.assignQA(this.task.id, this.selectedUserId, this.isPrinciple).subscribe({
          next: () => {
            this.loadTask(this.task!.id);
            this.showAssignModal = false;
          }
        });
      }
    }
  }

  getStatusClass(status: string): string {
    return 'status-' + status.toLowerCase();
  }
}

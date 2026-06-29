import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { CaseService } from '../../../services/case.service';
import { TaskService } from '../../../services/task.service';
import { EvidenceService } from '../../../services/evidence.service';
import { AuthService } from '../../../services/auth.service';
import { Case, CaseHistory } from '../../../models/case.model';
import { Task } from '../../../models/task.model';
import { Evidence } from '../../../models/evidence.model';

@Component({
  selector: 'app-case-detail',
  templateUrl: './case-detail.component.html',
  styleUrls: ['./case-detail.component.css']
})
export class CaseDetailComponent implements OnInit {
  case: Case | null = null;
  tasks: Task[] = [];
  evidenceItems: Evidence[] = [];
  history: CaseHistory[] = [];
  activeTab = 'overview';
  loading = true;
  showStatusModal = false;
  statusForm = { status: '', reason: '' };

  caseFiles: any[] = [];
  selectedFiles: File[] = [];
  uploadNote = '';
  uploading = false;

  showViewer = false;
  viewerUrl: SafeResourceUrl = '';

  tabs = [
    { label: 'Overview', icon: 'info' },
    { label: 'Tasks', icon: 'task' },
    { label: 'Evidence', icon: 'fingerprint' },
    { label: 'History', icon: 'history' },
    { label: 'Files', icon: 'folder' },
    { label: 'Settings', icon: 'settings' }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private caseService: CaseService,
    private taskService: TaskService,
    private evidenceService: EvidenceService,
    public authService: AuthService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.loadCase(id);
      this.loadTasks(id);
      this.loadEvidence(id);
      this.loadHistory(id);
      this.loadCaseFiles(id);
    }
  }

  loadCase(id: number): void {
    this.caseService.getById(id).subscribe({
      next: (data) => {
        this.case = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.router.navigate(['/cases']);
      }
    });
  }

  loadTasks(id: number): void {
    this.caseService.getTasks(id).subscribe({
      next: (data) => this.tasks = data
    });
  }

  loadEvidence(id: number): void {
    this.caseService.getEvidence(id).subscribe({
      next: (data) => this.evidenceItems = data
    });
  }

  loadHistory(id: number): void {
    this.caseService.getHistory(id).subscribe({
      next: (data) => this.history = data
    });
  }

  setTab(tab: string): void {
    this.activeTab = tab;
  }

  changeStatus(): void {
    if (this.case) {
      this.caseService.changeStatus(this.case.id, this.statusForm.status, this.statusForm.reason).subscribe({
        next: () => {
          this.loadCase(this.case!.id);
          this.showStatusModal = false;
          this.statusForm = { status: '', reason: '' };
        }
      });
    }
  }

  closeCase(): void {
    const reason = prompt('Enter reason for closing:');
    if (reason && this.case) {
      this.caseService.close(this.case.id, reason).subscribe({
        next: () => this.loadCase(this.case!.id)
      });
    }
  }

  getStatusClass(status: string): string {
    return 'status-' + status.toLowerCase();
  }

  getPriorityClass(priority: string): string {
    return 'priority-' + priority.toLowerCase();
  }

  objectKeys(obj: any): string[] {
    return Object.keys(obj || {});
  }

  loadCaseFiles(caseId: number): void {
    this.caseService.getDocuments(caseId).subscribe({
      next: (data) => this.caseFiles = data
    });
  }

  onFilesSelected(event: any): void {
    const files: FileList = event.target.files;
    this.selectedFiles = [];
    for (let i = 0; i < files.length; i++) {
      this.selectedFiles.push(files[i]);
    }
  }

  uploadFiles(): void {
    if (this.selectedFiles.length === 0 || !this.case) return;
    this.uploading = true;
    this.caseService.uploadDocuments(this.case.id, this.selectedFiles, this.uploadNote).subscribe({
      next: () => {
        this.loadCaseFiles(this.case!.id);
        this.selectedFiles = [];
        this.uploadNote = '';
        this.uploading = false;
      },
      error: () => {
        this.uploading = false;
      }
    });
  }

  downloadFile(location: string): void {
    window.open('http://localhost:8080' + location, '_blank');
  }

  viewFile(location: string): void {
    const url = 'http://localhost:8080' + location;
    this.viewerUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
    this.showViewer = true;
  }

  closeViewer(): void {
    this.showViewer = false;
    this.viewerUrl = '';
  }

  isImageFile(filename: string): boolean {
    return /\.(jpg|jpeg|png|gif|bmp|webp)$/i.test(filename);
  }

  formatFileSize(bytes: number): string {
    if (!bytes) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return '';
    return new Date(dateStr).toLocaleString();
  }
}

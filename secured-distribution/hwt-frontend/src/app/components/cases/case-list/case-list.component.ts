import { Component, OnInit } from '@angular/core';
import { CaseService } from '../../../services/case.service';
import { AuthService } from '../../../services/auth.service';
import { Case } from '../../../models/case.model';

@Component({
  selector: 'app-case-list',
  templateUrl: './case-list.component.html',
  styleUrls: ['./case-list.component.css']
})
export class CaseListComponent implements OnInit {
  cases: Case[] = [];
  filteredCases: Case[] = [];
  activeFilter = 'all';
  loading = true;

  showImportModal = false;
  importFile: File | null = null;
  importing = false;
  importResult: any = null;

  filters = [
    { key: 'all', label: 'All' },
    { key: 'OPEN', label: 'Open' },
    { key: 'CREATED', label: 'Created' },
    { key: 'PENDING', label: 'Pending' },
    { key: 'CLOSED', label: 'Closed' },
    { key: 'ARCHIVED', label: 'Archived' }
  ];

  constructor(
    private caseService: CaseService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadCases();
  }

  loadCases(): void {
    this.loading = true;
    this.caseService.getAll().subscribe({
      next: (data) => {
        this.cases = data;
        this.applyFilter();
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  setFilter(filter: string): void {
    if (this.activeFilter === filter) return;
    this.activeFilter = filter;
    this.applyFilter();
  }

  applyFilter(): void {
    if (this.activeFilter === 'all') {
      this.filteredCases = [...this.cases];
    } else {
      this.filteredCases = this.cases.filter(c => c.currentStatus === this.activeFilter);
    }
  }

  getStatusClass(status: string): string {
    return 'status-' + status.toLowerCase();
  }

  getPriorityClass(priority: string): string {
    return 'priority-' + priority.toLowerCase();
  }

  trackById(index: number, item: Case): number {
    return item.id;
  }

  openImportModal(): void {
    this.showImportModal = true;
    this.importFile = null;
    this.importResult = null;
  }

  closeImportModal(): void {
    this.showImportModal = false;
    this.importFile = null;
    this.importResult = null;
  }

  onImportFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.importFile = input.files[0];
      this.importResult = null;
    }
  }

  downloadTemplate(): void {
    window.open(this.caseService.downloadTemplate(), '_blank');
  }

  importCases(): void {
    if (!this.importFile) return;
    this.importing = true;
    this.caseService.importCases(this.importFile).subscribe({
      next: (result) => {
        this.importResult = result;
        this.importing = false;
        if (result.successCount > 0) {
          this.loadCases();
        }
      },
      error: (err) => {
        let msg = 'Import failed';
        if (err.error) {
          if (typeof err.error === 'string') msg = err.error;
          else if (err.error.error) msg = err.error.error;
          else if (err.error.message) msg = err.error.message;
        }
        if (msg === 'Import failed' && err.message) msg = err.message;
        this.importResult = { errorCount: 1, successCount: 0, results: [{ success: false, error: msg }] };
        this.importing = false;
      }
    });
  }
}

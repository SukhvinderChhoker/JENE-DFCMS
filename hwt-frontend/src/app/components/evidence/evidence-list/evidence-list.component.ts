import { Component, OnInit } from '@angular/core';
import { EvidenceService } from '../../../services/evidence.service';
import { AuthService } from '../../../services/auth.service';
import { Evidence } from '../../../models/evidence.model';

@Component({
  selector: 'app-evidence-list',
  templateUrl: './evidence-list.component.html',
  styleUrls: ['./evidence-list.component.css']
})
export class EvidenceListComponent implements OnInit {
  evidenceItems: Evidence[] = [];
  loading = true;

  showImportModal = false;
  importFile: File | null = null;
  importing = false;
  importResult: any = null;

  typeIcons: { [key: string]: string } = {
    'PC': 'computer',
    'Laptop': 'laptop',
    'Ext HDD/SSD': 'storage',
    'Mobile': 'smartphone',
    'Smart Device': 'devices_other',
    'OM': 'dns'
  };

  constructor(
    private evidenceService: EvidenceService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadEvidence();
  }

  loadEvidence(): void {
    this.evidenceService.getAll().subscribe({
      next: (data) => {
        this.evidenceItems = data;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  getTypeIcon(type: string): string {
    return this.typeIcons[type] || 'category';
  }

  getStatusClass(status: string): string {
    return 'status-' + status.toLowerCase();
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
    window.open(this.evidenceService.downloadTemplate(), '_blank');
  }

  importEvidence(): void {
    if (!this.importFile) return;
    this.importing = true;
    this.evidenceService.importEvidence(this.importFile).subscribe({
      next: (result) => {
        this.importResult = result;
        this.importing = false;
        if (result.successCount > 0) {
          this.loadEvidence();
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

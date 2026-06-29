import { Component, OnInit } from '@angular/core';
import { CaseService } from '../../services/case.service';
import { FinalReportService } from '../../services/final-report.service';
import { AuthService } from '../../services/auth.service';
import { Case } from '../../models/case.model';

@Component({
  selector: 'app-final-reports',
  templateUrl: './final-reports.component.html',
  styleUrls: ['./final-reports.component.css']
})
export class FinalReportsComponent implements OnInit {
  cases: Case[] = [];
  selectedCaseId: number = 0;
  reports: any[] = [];
  loading = false;
  uploading = false;
  selectedFile: File | null = null;
  reportType = '';
  reportSummary = '';
  showUploadForm = false;
  successMsg = '';
  errorMsg = '';
  dragOver = false;

  reportTypes = [
    'Final Investigation Report',
    'Technical Analysis Report',
    'Forensic Examination Report',
    'Incident Response Report',
    'Malware Analysis Report',
    'Network Forensics Report',
    'Memory Forensics Report',
    'Mobile Forensics Report',
    'Cloud Forensics Report',
    'Other'
  ];

  constructor(
    private caseService: CaseService,
    private finalReportService: FinalReportService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadCases();
  }

  loadCases(): void {
    this.caseService.getAll().subscribe({
      next: (data) => this.cases = data,
      error: () => this.errorMsg = 'Failed to load cases'
    });
  }

  onCaseSelect(): void {
    if (this.selectedCaseId) {
      this.loadReports();
    } else {
      this.reports = [];
    }
  }

  loadReports(): void {
    this.loading = true;
    this.finalReportService.getFinalReports(this.selectedCaseId).subscribe({
      next: (data) => {
        this.reports = data;
        this.loading = false;
      },
      error: () => {
        this.errorMsg = 'Failed to load reports';
        this.loading = false;
      }
    });
  }

  toggleUploadForm(): void {
    this.showUploadForm = !this.showUploadForm;
    this.clearForm();
  }

  onFileSelect(event: any): void {
    const files = event.target.files;
    if (files && files.length > 0) {
      this.selectedFile = files[0];
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.dragOver = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.dragOver = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.dragOver = false;
    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.selectedFile = files[0];
    }
  }

  uploadReport(): void {
    if (!this.selectedFile || !this.selectedCaseId) {
      this.errorMsg = 'Please select a case and a file';
      return;
    }

    this.uploading = true;
    this.errorMsg = '';
    this.successMsg = '';

    this.finalReportService.uploadFinalReport(
      this.selectedCaseId,
      this.selectedFile,
      this.reportType,
      this.reportSummary
    ).subscribe({
      next: (result) => {
        this.successMsg = result.message || 'Report uploaded successfully';
        this.uploading = false;
        this.showUploadForm = false;
        this.clearForm();
        this.loadReports();
        setTimeout(() => this.successMsg = '', 5000);
      },
      error: (err) => {
        this.errorMsg = err.error?.message || 'Failed to upload report';
        this.uploading = false;
      }
    });
  }

  deleteReport(report: any): void {
    if (!confirm('Are you sure you want to delete this report?')) return;

    this.finalReportService.deleteFinalReport(report.id).subscribe({
      next: () => {
        this.successMsg = 'Report deleted successfully';
        this.loadReports();
        setTimeout(() => this.successMsg = '', 5000);
      },
      error: (err) => {
        this.errorMsg = err.error?.message || 'Failed to delete report';
      }
    });
  }

  downloadReport(report: any): void {
    if (report.uploadLocation) {
      window.open('http://localhost:8080' + report.uploadLocation, '_blank');
    }
  }

  formatFileSize(bytes: number): string {
    if (!bytes || bytes === 0) return '0 B';
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(1024));
    return (bytes / Math.pow(1024, i)).toFixed(1) + ' ' + sizes[i];
  }

  getCaseName(caseId: number): string {
    const c = this.cases.find(c => c.id === caseId);
    return c ? c.caseName : 'Unknown';
  }

  clearForm(): void {
    this.selectedFile = null;
    this.reportType = '';
    this.reportSummary = '';
    this.errorMsg = '';
  }
}

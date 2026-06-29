import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { EvidenceService } from '../../../services/evidence.service';
import { AuthService } from '../../../services/auth.service';
import { Evidence, ChainOfCustody } from '../../../models/evidence.model';

@Component({
  selector: 'app-evidence-detail',
  templateUrl: './evidence-detail.component.html',
  styleUrls: ['./evidence-detail.component.css']
})
export class EvidenceDetailComponent implements OnInit {
  evidence: Evidence | null = null;
  chainOfCustody: ChainOfCustody[] = [];
  loading = true;
  showCheckInModal = false;
  showCheckOutModal = false;
  showStatusModal = false;
  checkForm = { custodian: '', date: '', comment: '' };
  statusForm = { status: '', note: '' };

  evidenceDocuments: any[] = [];
  selectedPhoto: File | null = null;
  selectedDoc: File | null = null;
  docNote = '';
  uploadingPhoto = false;
  uploadingDoc = false;

  showViewer = false;
  viewerUrl: SafeResourceUrl = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private evidenceService: EvidenceService,
    public authService: AuthService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.loadEvidence(id);
      this.loadChainOfCustody(id);
      this.loadDocuments(id);
    }
  }

  loadEvidence(id: number): void {
    this.evidenceService.getById(id).subscribe({
      next: (data) => {
        this.evidence = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.router.navigate(['/evidence']);
      }
    });
  }

  loadChainOfCustody(id: number): void {
    this.evidenceService.getChainOfCustody(id).subscribe({
      next: (data) => this.chainOfCustody = data
    });
  }

  openCheckIn(): void {
    this.checkForm = { custodian: '', date: new Date().toISOString().slice(0, 16), comment: '' };
    this.showCheckInModal = true;
  }

  openCheckOut(): void {
    this.checkForm = { custodian: '', date: new Date().toISOString().slice(0, 16), comment: '' };
    this.showCheckOutModal = true;
  }

  confirmCheckIn(): void {
    if (this.evidence) {
      this.evidenceService.checkIn(this.evidence.id, this.checkForm.custodian, this.checkForm.date, this.checkForm.comment).subscribe({
        next: () => {
          this.loadEvidence(this.evidence!.id);
          this.loadChainOfCustody(this.evidence!.id);
          this.showCheckInModal = false;
        }
      });
    }
  }

  confirmCheckOut(): void {
    if (this.evidence) {
      this.evidenceService.checkOut(this.evidence.id, this.checkForm.custodian, this.checkForm.date, this.checkForm.comment).subscribe({
        next: () => {
          this.loadEvidence(this.evidence!.id);
          this.loadChainOfCustody(this.evidence!.id);
          this.showCheckOutModal = false;
        }
      });
    }
  }

  changeStatus(): void {
    if (this.evidence) {
      this.evidenceService.changeStatus(this.evidence.id, this.statusForm.status, this.statusForm.note).subscribe({
        next: () => {
          this.loadEvidence(this.evidence!.id);
          this.showStatusModal = false;
        }
      });
    }
  }

  getStatusClass(status: string): string {
    return 'status-' + status.toLowerCase();
  }

  loadDocuments(id: number): void {
    this.evidenceService.getDocuments(id).subscribe({
      next: (data) => this.evidenceDocuments = data
    });
  }

  onPhotoSelected(event: any): void {
    const files: FileList = event.target.files;
    if (files.length > 0) {
      this.selectedPhoto = files[0];
    }
  }

  uploadPhoto(): void {
    if (!this.selectedPhoto || !this.evidence) return;
    this.uploadingPhoto = true;
    this.evidenceService.uploadPhoto(this.evidence.id, this.selectedPhoto).subscribe({
      next: () => {
        this.loadEvidence(this.evidence!.id);
        this.selectedPhoto = null;
        this.uploadingPhoto = false;
      },
      error: () => this.uploadingPhoto = false
    });
  }

  onDocSelected(event: any): void {
    const files: FileList = event.target.files;
    if (files.length > 0) {
      this.selectedDoc = files[0];
    }
  }

  uploadDocument(): void {
    if (!this.selectedDoc || !this.evidence) return;
    this.uploadingDoc = true;
    this.evidenceService.uploadDocument(this.evidence.id, this.selectedDoc, this.docNote).subscribe({
      next: () => {
        this.loadDocuments(this.evidence!.id);
        this.selectedDoc = null;
        this.docNote = '';
        this.uploadingDoc = false;
      },
      error: () => this.uploadingDoc = false
    });
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
}

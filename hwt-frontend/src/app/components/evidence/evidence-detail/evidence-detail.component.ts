import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EvidenceService } from '../../../services/evidence.service';
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

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private evidenceService: EvidenceService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.loadEvidence(id);
      this.loadChainOfCustody(id);
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
}

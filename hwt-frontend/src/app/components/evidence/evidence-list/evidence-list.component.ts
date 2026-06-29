import { Component, OnInit } from '@angular/core';
import { EvidenceService } from '../../../services/evidence.service';
import { Evidence } from '../../../models/evidence.model';

@Component({
  selector: 'app-evidence-list',
  templateUrl: './evidence-list.component.html',
  styleUrls: ['./evidence-list.component.css']
})
export class EvidenceListComponent implements OnInit {
  evidenceItems: Evidence[] = [];
  loading = true;

  typeIcons: { [key: string]: string } = {
    'PC': 'computer',
    'Laptop': 'laptop',
    'Ext HDD/SSD': 'storage',
    'Mobile': 'smartphone',
    'Smart Device': 'devices_other',
    'OM': 'dns'
  };

  constructor(private evidenceService: EvidenceService) {}

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
}

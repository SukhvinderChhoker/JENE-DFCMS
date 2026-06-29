import { Component, OnInit } from '@angular/core';
import { CaseService } from '../../../services/case.service';
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

  filters = [
    { key: 'all', label: 'All' },
    { key: 'OPEN', label: 'Open' },
    { key: 'CREATED', label: 'Created' },
    { key: 'PENDING', label: 'Pending' },
    { key: 'CLOSED', label: 'Closed' },
    { key: 'ARCHIVED', label: 'Archived' }
  ];

  constructor(private caseService: CaseService) {}

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
    this.activeFilter = filter;
    this.applyFilter();
  }

  applyFilter(): void {
    if (this.activeFilter === 'all') {
      this.filteredCases = this.cases;
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
}

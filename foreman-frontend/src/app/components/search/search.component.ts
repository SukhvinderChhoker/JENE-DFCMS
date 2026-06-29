import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CaseService } from '../../services/case.service';
import { Case } from '../../models/case.model';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {
  cases: Case[] = [];
  filteredCases: Case[] = [];
  loading = false;

  searchQuery = '';
  dateFrom = '';
  dateTo = '';
  caseType = '';
  priority = '';
  status = '';
  deviceType = '';

  caseTypes = ['Investigation', 'Enquiry', 'Incident', 'Other'];
  priorities = ['Low', 'Normal', 'High', 'Critical'];
  statuses = ['CREATED', 'PENDING', 'OPEN', 'IN_PROGRESS', 'PENDING_AUTH', 'AUTHORIZED', 'REJECTED', 'CLOSED', 'ARCHIVED'];
  deviceTypes = ['SATA Hard Drive', 'IDE Hard Drive', 'Mobile Phone', 'Smart Phone', 'Tablet', 'USB Media', 'Laptop', 'Server', 'CD', 'DVD'];

  chartData: { label: string; value: number; color: string }[] = [];
  priorityChartData: { label: string; value: number; color: string }[] = [];
  statusChartData: { label: string; value: number; color: string }[] = [];

  constructor(private caseService: CaseService, private router: Router) {}

  ngOnInit(): void {
    this.loadCases();
  }

  loadCases(): void {
    this.loading = true;
    this.caseService.getAll().subscribe({
      next: (data) => {
        this.cases = data;
        this.filteredCases = [...data];
        this.updateCharts();
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  applyFilters(): void {
    this.filteredCases = this.cases.filter(c => {
      if (this.searchQuery) {
        const q = this.searchQuery.toLowerCase();
        const matchName = (c.caseName || '').toLowerCase().includes(q);
        const matchRef = (c.reference || '').toLowerCase().includes(q);
        const matchBg = (c.background || '').toLowerCase().includes(q);
        if (!matchName && !matchRef && !matchBg) return false;
      }
      if (this.caseType && c.caseType !== this.caseType) return false;
      if (this.priority && c.casePriority !== this.priority) return false;
      if (this.status && c.currentStatus !== this.status) return false;
      if (this.dateFrom || this.dateTo) {
        const created = c.creationDate ? new Date(c.creationDate) : null;
        if (!created) return false;
        if (this.dateFrom && created < new Date(this.dateFrom)) return false;
        if (this.dateTo && created > new Date(this.dateTo + 'T23:59:59')) return false;
      }
      return true;
    });
    this.updateCharts();
  }

  clearFilters(): void {
    this.searchQuery = '';
    this.dateFrom = '';
    this.dateTo = '';
    this.caseType = '';
    this.priority = '';
    this.status = '';
    this.deviceType = '';
    this.filteredCases = [...this.cases];
    this.updateCharts();
  }

  updateCharts(): void {
    const typeCounts: Record<string, number> = {};
    const priorityCounts: Record<string, number> = {};
    const statusCounts: Record<string, number> = {};

    this.filteredCases.forEach(c => {
      const t = c.caseType || 'Unknown';
      typeCounts[t] = (typeCounts[t] || 0) + 1;
      const p = c.casePriority || 'Unknown';
      priorityCounts[p] = (priorityCounts[p] || 0) + 1;
      const s = c.currentStatus || 'Unknown';
      statusCounts[s] = (statusCounts[s] || 0) + 1;
    });

    const typeColors = ['#1a237e', '#ff6f00', '#2e7d32', '#c62828', '#6a1b9a', '#00838f'];
    this.chartData = Object.entries(typeCounts).map(([label, value], i) => ({
      label, value, color: typeColors[i % typeColors.length]
    }));

    const priColors = { Low: '#00CCFF', Normal: '#009900', High: '#FF9933', Critical: '#CC0000' };
    this.priorityChartData = Object.entries(priorityCounts).map(([label, value]) => ({
      label, value, color: (priColors as any)[label] || '#999'
    }));

    const statusColors = ['#78909c', '#ff9800', '#2196f3', '#4caf50', '#ff5722', '#9c27b0', '#e91e63', '#607d8b', '#795548'];
    this.statusChartData = Object.entries(statusCounts).map(([label, value], i) => ({
      label, value, color: statusColors[i % statusColors.length]
    }));
  }

  getMaxValue(): number {
    const all = [...this.chartData, ...this.priorityChartData, ...this.statusChartData];
    return Math.max(...all.map(d => d.value), 1);
  }

  getStatusClass(status: string): string {
    const map: Record<string, string> = {
      'CREATED': 'status-created', 'PENDING': 'status-pending', 'OPEN': 'status-open',
      'IN_PROGRESS': 'status-progress', 'PENDING_AUTH': 'status-pending-auth',
      'AUTHORIZED': 'status-authorized', 'REJECTED': 'status-rejected',
      'CLOSED': 'status-closed', 'ARCHIVED': 'status-archived'
    };
    return map[status] || '';
  }

  getPriorityClass(priority: string): string {
    const map: Record<string, string> = {
      'Low': 'priority-low', 'Normal': 'priority-normal',
      'High': 'priority-high', 'Critical': 'priority-critical'
    };
    return map[priority] || '';
  }

  navigateToCase(id: number): void {
    this.router.navigate(['/cases', id]);
  }
}

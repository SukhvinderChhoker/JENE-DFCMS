import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DashboardStats } from '../models/dashboard.model';
import { Case, CaseHistory } from '../models/case.model';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private apiUrl = 'http://localhost:8080/api/reports';

  constructor(private http: HttpClient) {}

  getDashboard(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(`${this.apiUrl}/dashboard`);
  }

  getCaseReport(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/cases/${id}`);
  }

  getCaseActivity(id: number): Observable<CaseHistory[]> {
    return this.http.get<CaseHistory[]>(`${this.apiUrl}/cases/${id}/activity`);
  }
}

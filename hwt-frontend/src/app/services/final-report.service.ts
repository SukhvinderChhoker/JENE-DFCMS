import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FinalReportService {
  private apiUrl = 'http://localhost:8080/api/uploads';

  constructor(private http: HttpClient) {}

  getFinalReports(caseId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/case/${caseId}/final-report`);
  }

  uploadFinalReport(caseId: number, file: File, reportType: string, summary: string): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    if (reportType) formData.append('reportType', reportType);
    if (summary) formData.append('summary', summary);
    return this.http.post(`${this.apiUrl}/case/${caseId}/final-report`, formData);
  }

  deleteFinalReport(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/final-report/${id}`);
  }
}

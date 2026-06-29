import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Case, CaseHistory } from '../models/case.model';
import { Task } from '../models/task.model';
import { Evidence } from '../models/evidence.model';

@Injectable({
  providedIn: 'root'
})
export class CaseService {
  private apiUrl = 'http://localhost:8080/api/cases';

  constructor(private http: HttpClient) {}

  getAll(status?: string): Observable<Case[]> {
    let params = new HttpParams();
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<Case[]>(this.apiUrl, { params });
  }

  getById(id: number): Observable<Case> {
    return this.http.get<Case>(`${this.apiUrl}/${id}`);
  }

  create(caseData: Partial<Case>): Observable<Case> {
    return this.http.post<Case>(this.apiUrl, caseData);
  }

  update(id: number, caseData: Partial<Case>): Observable<Case> {
    return this.http.put<Case>(`${this.apiUrl}/${id}`, caseData);
  }

  changeStatus(id: number, status: string, reason: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/status`, { status, reason });
  }

  close(id: number, reason: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/close`, { reason });
  }

  authorize(id: number, authorize: boolean, reason: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/authorize`, { authorize, reason });
  }

  getHistory(id: number): Observable<CaseHistory[]> {
    return this.http.get<CaseHistory[]>(`${this.apiUrl}/${id}/history`);
  }

  getTasks(id: number): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/${id}/tasks`);
  }

  getEvidence(id: number): Observable<Evidence[]> {
    return this.http.get<Evidence[]>(`${this.apiUrl}/${id}/evidence`);
  }

  linkCases(id: number, linkedId: number, reason: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/${id}/link`, { linkedId, reason });
  }

  unlinkCases(id: number, linkedId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}/link/${linkedId}`);
  }

  uploadDocument(caseId: number, file: File, note: string): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    if (note) formData.append('note', note);
    return this.http.post(`http://localhost:8080/api/uploads/case/${caseId}`, formData);
  }

  getDocuments(caseId: number): Observable<any[]> {
    return this.http.get<any[]>(`http://localhost:8080/api/uploads/case/${caseId}`);
  }
}

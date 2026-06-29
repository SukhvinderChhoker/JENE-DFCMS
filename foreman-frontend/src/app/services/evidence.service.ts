import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Evidence, ChainOfCustody } from '../models/evidence.model';

@Injectable({
  providedIn: 'root'
})
export class EvidenceService {
  private apiUrl = 'http://localhost:8080/api/evidence';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Evidence[]> {
    return this.http.get<Evidence[]>(this.apiUrl);
  }

  getById(id: number): Observable<Evidence> {
    return this.http.get<Evidence>(`${this.apiUrl}/${id}`);
  }

  create(caseId: number, evidence: Partial<Evidence>): Observable<Evidence> {
    return this.http.post<Evidence>(`http://localhost:8080/api/cases/${caseId}/evidence`, evidence);
  }

  update(id: number, evidence: Partial<Evidence>): Observable<Evidence> {
    return this.http.put<Evidence>(`${this.apiUrl}/${id}`, evidence);
  }

  changeStatus(id: number, status: string, note: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/status`, { status, note });
  }

  checkIn(id: number, custodian: string, date: string, comment: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/check-in`, { custodian, date, comment });
  }

  checkOut(id: number, custodian: string, date: string, comment: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/check-out`, { custodian, date, comment });
  }

  getChainOfCustody(id: number): Observable<ChainOfCustody[]> {
    return this.http.get<ChainOfCustody[]>(`${this.apiUrl}/${id}/chain-of-custody`);
  }

  getHistory(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${id}/history`);
  }

  uploadPhoto(id: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/${id}/photo`, formData);
  }

  uploadDocument(id: number, file: File, note: string): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    if (note) formData.append('note', note);
    return this.http.post(`${this.apiUrl}/${id}/document`, formData);
  }

  getDocuments(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${id}/documents`);
  }
}

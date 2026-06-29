import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task, TaskNote } from '../models/task.model';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private apiUrl = 'http://localhost:8080/api/tasks';

  constructor(private http: HttpClient) {}

  getByCase(caseId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`http://localhost:8080/api/cases/${caseId}/tasks`);
  }

  getById(id: number): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/${id}`);
  }

  create(caseId: number, task: Partial<Task>): Observable<Task> {
    return this.http.post<Task>(`http://localhost:8080/api/cases/${caseId}/tasks`, task);
  }

  update(id: number, task: Partial<Task>): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/${id}`, task);
  }

  changeStatus(id: number, status: string, note: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/status`, { status, note });
  }

  assignInvestigator(id: number, userId: number, principle: boolean): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/assign-investigator`, { userId, principle });
  }

  assignQA(id: number, userId: number, principle: boolean): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/assign-qa`, { userId, principle });
  }

  getNotes(id: number): Observable<TaskNote[]> {
    return this.http.get<TaskNote[]>(`${this.apiUrl}/${id}/notes`);
  }

  addNote(id: number, note: string): Observable<TaskNote> {
    return this.http.post<TaskNote>(`${this.apiUrl}/${id}/notes`, { note });
  }

  getHistory(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${id}/history`);
  }
}

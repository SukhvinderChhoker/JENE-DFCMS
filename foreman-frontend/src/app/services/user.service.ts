import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  getAll(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  getById(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }

  update(id: number, user: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, user);
  }

  create(user: Partial<User>): Observable<User> {
    return this.http.post<User>(this.apiUrl, user);
  }

  getByRole(role: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}?role=${role}`);
  }

  changePassword(id: number, password: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/password`, { password });
  }
}

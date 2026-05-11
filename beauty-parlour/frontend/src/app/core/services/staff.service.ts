import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class StaffService {
  private readonly base = `${environment.apiBaseUrl}/staff`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<User[]> {
    return this.http.get<User[]>(this.base);
  }

  add(payload: { name: string; email: string; password: string; phone?: string }): Observable<User> {
    return this.http.post<User>(this.base, payload);
  }

  update(id: string, payload: { name: string; email: string; phone?: string }): Observable<User> {
    return this.http.put<User>(`${this.base}/${id}`, payload);
  }

  remove(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}

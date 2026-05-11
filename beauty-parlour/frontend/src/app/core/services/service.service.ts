import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { BeautyService, ServiceCategory } from '../models/service.model';

@Injectable({ providedIn: 'root' })
export class ServiceService {
  private readonly base = `${environment.apiBaseUrl}/services`;

  constructor(private http: HttpClient) {}

  getAll(category?: ServiceCategory): Observable<BeautyService[]> {
    const params: Record<string, string> = {};
    if (category) params['category'] = category;
    return this.http.get<BeautyService[]>(this.base, { params });
  }

  create(payload: Partial<BeautyService>): Observable<BeautyService> {
    return this.http.post<BeautyService>(this.base, payload);
  }

  update(id: string, payload: Partial<BeautyService>): Observable<BeautyService> {
    return this.http.put<BeautyService>(`${this.base}/${id}`, payload);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}

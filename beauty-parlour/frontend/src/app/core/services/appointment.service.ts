import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Appointment, BookingRequest } from '../models/appointment.model';

@Injectable({ providedIn: 'root' })
export class AppointmentService {
  private readonly base = `${environment.apiBaseUrl}/appointments`;

  constructor(private http: HttpClient) {}

  getAvailability(staffId: string, serviceId: string, date: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.base}/availability`, {
      params: { staffId, serviceId, date }
    });
  }

  book(payload: BookingRequest): Observable<Appointment> {
    return this.http.post<Appointment>(this.base, payload);
  }

  getMyAppointments(): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(this.base);
  }

  cancel(id: string): Observable<Appointment> {
    return this.http.patch<Appointment>(`${this.base}/${id}/cancel`, {});
  }
}

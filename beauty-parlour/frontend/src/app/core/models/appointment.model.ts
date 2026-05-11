import { BeautyService } from './service.model';
import { User } from './user.model';

export type AppointmentStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';

export interface Appointment {
  id: string;
  status: AppointmentStatus;
  startTime: string;
  endTime: string;
  notes: string;
  service: BeautyService;
  staff: User;
  customer: User;
}

export interface BookingRequest {
  staffId: string;
  serviceId: string;
  startTime: string;
  notes?: string;
}

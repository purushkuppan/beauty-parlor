import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Appointment } from '../../core/models/appointment.model';
import { BeautyService } from '../../core/models/service.model';
import { User } from '../../core/models/user.model';
import { AppointmentService } from '../../core/services/appointment.service';
import { ServiceService } from '../../core/services/service.service';
import { StaffService } from '../../core/services/staff.service';

@Component({
  selector: 'app-booking',
  templateUrl: './booking.component.html',
  standalone: false
})
export class BookingComponent implements OnInit {
  form: FormGroup;
  services: BeautyService[] = [];
  staff: User[] = [];
  slots: string[] = [];
  myAppointments: Appointment[] = [];
  readonly today = new Date().toISOString().split('T')[0];
  loading = false;
  error = '';
  success = '';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private appointmentService: AppointmentService,
    private serviceService: ServiceService,
    private staffService: StaffService
  ) {
    this.form = this.fb.group({
      serviceId: ['', Validators.required],
      staffId: ['', Validators.required],
      date: ['', Validators.required],
      slot: ['', Validators.required],
      notes: ['']
    });
  }

  ngOnInit(): void {
    this.serviceService.getAll().subscribe(s => this.services = s);
    this.staffService.getAll().subscribe(s => this.staff = s);
    this.appointmentService.getMyAppointments().subscribe(a => this.myAppointments = a);

    const { serviceId, staffId } = this.route.snapshot.queryParams;
    if (serviceId) this.form.patchValue({ serviceId });
    if (staffId) this.form.patchValue({ staffId });
  }

  loadSlots(): void {
    const { staffId, serviceId, date } = this.form.value;
    if (!staffId || !serviceId || !date) return;
    this.appointmentService.getAvailability(staffId, serviceId, date).subscribe({
      next: res => this.slots = res,
      error: err => this.error = err.message
    });
  }

  book(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.error = '';
    const { serviceId, staffId, date, slot, notes } = this.form.value;
    const startTime = `${date}T${slot}:00Z`;
    this.appointmentService.book({ serviceId, staffId, startTime, notes }).subscribe({
      next: () => {
        this.success = 'Appointment booked successfully!';
        this.loading = false;
        this.appointmentService.getMyAppointments().subscribe(a => this.myAppointments = a);
      },
      error: err => { this.error = err.message; this.loading = false; }
    });
  }

  cancel(id: string): void {
    this.appointmentService.cancel(id).subscribe({
      next: () => this.appointmentService.getMyAppointments().subscribe(a => this.myAppointments = a),
      error: err => this.error = err.message
    });
  }
}

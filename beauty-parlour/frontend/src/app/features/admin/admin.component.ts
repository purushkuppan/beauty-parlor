import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Appointment } from '../../core/models/appointment.model';
import { BeautyService } from '../../core/models/service.model';
import { User } from '../../core/models/user.model';
import { AppointmentService } from '../../core/services/appointment.service';
import { ServiceService } from '../../core/services/service.service';
import { StaffService } from '../../core/services/staff.service';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  standalone: false
})
export class AdminComponent implements OnInit {
  appointments: Appointment[] = [];
  services: BeautyService[] = [];
  staff: User[] = [];
  activeTab: 'appointments' | 'services' | 'team' = 'appointments';
  error = '';
  success = '';

  showServiceForm = false;
  showStaffForm = false;
  serviceForm: FormGroup;
  staffForm: FormGroup;
  savingService = false;
  savingStaff = false;

  readonly categories = ['HAIR', 'SKIN', 'NAILS', 'MAKEUP'] as const;

  constructor(
    private fb: FormBuilder,
    private appointmentService: AppointmentService,
    private serviceService: ServiceService,
    private staffService: StaffService
  ) {
    this.serviceForm = this.fb.group({
      name:        ['', [Validators.required, Validators.maxLength(100)]],
      description: ['', Validators.maxLength(500)],
      category:    ['', Validators.required],
      price:       [null, [Validators.required, Validators.min(0.01)]],
      durationMins:[null, [Validators.required, Validators.min(15), Validators.max(480)]]
    });

    this.staffForm = this.fb.group({
      name:     ['', [Validators.required, Validators.maxLength(100)]],
      email:    ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.pattern('.*\\d.*')]],
      phone:    ['']
    });
  }

  ngOnInit(): void {
    this.loadAppointments();
    this.loadServices();
    this.loadStaff();
  }

  loadAppointments(): void {
    this.appointmentService.getMyAppointments().subscribe({
      next: data => this.appointments = data,
      error: err => this.error = err.message
    });
  }

  loadServices(): void {
    this.serviceService.getAll().subscribe({
      next: data => this.services = data,
      error: err => this.error = err.message
    });
  }

  loadStaff(): void {
    this.staffService.getAll().subscribe({
      next: data => this.staff = data,
      error: err => this.error = err.message
    });
  }

  addService(): void {
    if (this.serviceForm.invalid) return;
    this.savingService = true;
    this.error = '';
    this.serviceService.create(this.serviceForm.value).subscribe({
      next: () => {
        this.success = 'Service added successfully.';
        this.serviceForm.reset();
        this.showServiceForm = false;
        this.savingService = false;
        this.loadServices();
      },
      error: err => { this.error = err.message; this.savingService = false; }
    });
  }

  deleteService(id: string): void {
    if (!confirm('Deactivate this service?')) return;
    this.serviceService.delete(id).subscribe({
      next: () => { this.success = 'Service deactivated.'; this.loadServices(); },
      error: err => this.error = err.message
    });
  }

  addStaff(): void {
    if (this.staffForm.invalid) return;
    this.savingStaff = true;
    this.error = '';
    this.staffService.add(this.staffForm.value).subscribe({
      next: () => {
        this.success = 'Team member added successfully.';
        this.staffForm.reset();
        this.showStaffForm = false;
        this.savingStaff = false;
        this.loadStaff();
      },
      error: err => { this.error = err.message; this.savingStaff = false; }
    });
  }

  removeStaff(id: string): void {
    if (!confirm('Remove this team member?')) return;
    this.staffService.remove(id).subscribe({
      next: () => { this.success = 'Team member removed.'; this.loadStaff(); },
      error: err => this.error = err.message
    });
  }

  cancelAppointment(id: string): void {
    this.appointmentService.cancel(id).subscribe({
      next: () => this.loadAppointments(),
      error: err => this.error = err.message
    });
  }

  clearMessages(): void {
    this.error = '';
    this.success = '';
  }
}

import { Component, OnInit } from '@angular/core';
import { User } from '../../core/models/user.model';
import { StaffService } from '../../core/services/staff.service';

@Component({
  selector: 'app-staff',
  templateUrl: './staff.component.html',
  standalone: false
})
export class StaffComponent implements OnInit {
  staff: User[] = [];
  loading = false;
  error = '';

  constructor(private staffService: StaffService) {}

  ngOnInit(): void {
    this.loading = true;
    this.staffService.getAll().subscribe({
      next: data => { this.staff = data; this.loading = false; },
      error: err => { this.error = err.message; this.loading = false; }
    });
  }
}

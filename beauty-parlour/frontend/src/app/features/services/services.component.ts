import { Component, OnInit } from '@angular/core';
import { BeautyService, ServiceCategory } from '../../core/models/service.model';
import { ServiceService } from '../../core/services/service.service';

@Component({
  selector: 'app-services',
  templateUrl: './services.component.html',
  standalone: false
})
export class ServicesComponent implements OnInit {
  services: BeautyService[] = [];
  categories: ServiceCategory[] = ['HAIR', 'SKIN', 'NAILS', 'MAKEUP'];
  selectedCategory: ServiceCategory | undefined;
  loading = false;
  error = '';

  constructor(private serviceService: ServiceService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.serviceService.getAll(this.selectedCategory).subscribe({
      next: data => { this.services = data; this.loading = false; },
      error: err => { this.error = err.message; this.loading = false; }
    });
  }

  filter(cat: ServiceCategory | undefined): void {
    this.selectedCategory = cat;
    this.load();
  }
}

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { StaffComponent } from './staff.component';

@NgModule({
  declarations: [StaffComponent],
  imports: [
    CommonModule,
    RouterModule.forChild([{ path: '', component: StaffComponent }])
  ]
})
export class StaffModule {}

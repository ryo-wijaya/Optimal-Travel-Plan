import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { ViewBookingDetailsPage } from './view-booking-details.page';

const routes: Routes = [
  {
    path: '',
    component: ViewBookingDetailsPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ViewBookingDetailsPageRoutingModule {}

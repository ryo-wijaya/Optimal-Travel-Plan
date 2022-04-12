import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { CreateNewBookingPage } from './create-new-booking.page';

const routes: Routes = [
  {
    path: '',
    component: CreateNewBookingPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CreateNewBookingPageRoutingModule {}

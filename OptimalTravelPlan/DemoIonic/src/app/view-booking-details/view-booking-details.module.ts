import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { ViewBookingDetailsPageRoutingModule } from './view-booking-details-routing.module';

import { ViewBookingDetailsPage } from './view-booking-details.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    ViewBookingDetailsPageRoutingModule
  ],
  declarations: [ViewBookingDetailsPage]
})
export class ViewBookingDetailsPageModule {}

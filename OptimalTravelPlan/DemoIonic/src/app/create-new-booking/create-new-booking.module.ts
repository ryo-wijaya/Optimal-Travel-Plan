import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { CreateNewBookingPageRoutingModule } from './create-new-booking-routing.module';

import { CreateNewBookingPage } from './create-new-booking.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    CreateNewBookingPageRoutingModule
  ],
  declarations: [CreateNewBookingPage]
})
export class CreateNewBookingPageModule {}

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { TravelItineraryDetailsPageRoutingModule } from './travel-itinerary-details-routing.module';

import { TravelItineraryDetailsPage } from './travel-itinerary-details.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    TravelItineraryDetailsPageRoutingModule
  ],
  declarations: [TravelItineraryDetailsPage]
})
export class TravelItineraryDetailsPageModule {}

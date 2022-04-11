import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { TravelItinerariesPageRoutingModule } from './travel-itineraries-routing.module';

import { TravelItinerariesPage } from './travel-itineraries.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    TravelItinerariesPageRoutingModule
  ],
  declarations: [TravelItinerariesPage]
})
export class TravelItinerariesPageModule {}

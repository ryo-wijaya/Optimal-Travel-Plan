import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { TravelItinerariesPage } from './travel-itineraries.page';

const routes: Routes = [
  {
    path: '',
    component: TravelItinerariesPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TravelItinerariesPageRoutingModule {}

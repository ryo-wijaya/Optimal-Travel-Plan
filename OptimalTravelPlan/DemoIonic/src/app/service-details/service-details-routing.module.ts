import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { ServiceDetailsPage } from './service-details.page';

const routes: Routes = [
  {
    path: '',
    component: ServiceDetailsPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ServiceDetailsPageRoutingModule {}

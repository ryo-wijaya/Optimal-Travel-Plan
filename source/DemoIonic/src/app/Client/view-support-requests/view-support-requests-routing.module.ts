import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { ViewSupportRequestsPage } from './view-support-requests.page';

const routes: Routes = [
  {
    path: '',
    component: ViewSupportRequestsPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ViewSupportRequestsPageRoutingModule {}

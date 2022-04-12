import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { CreateSupportRequestPage } from './create-support-request.page';

const routes: Routes = [
  {
    path: '',
    component: CreateSupportRequestPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CreateSupportRequestPageRoutingModule {}

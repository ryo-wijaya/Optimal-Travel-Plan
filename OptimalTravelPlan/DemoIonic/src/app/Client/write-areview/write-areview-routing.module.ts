import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { WriteAReviewPage } from './write-areview.page';

const routes: Routes = [
  {
    path: '',
    component: WriteAReviewPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class WriteAReviewPageRoutingModule {}

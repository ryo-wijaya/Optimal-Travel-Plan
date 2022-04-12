import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { ViewSupportRequestsPageRoutingModule } from './view-support-requests-routing.module';

import { ViewSupportRequestsPage } from './view-support-requests.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    ViewSupportRequestsPageRoutingModule
  ],
  declarations: [ViewSupportRequestsPage]
})
export class ViewSupportRequestsPageModule {}

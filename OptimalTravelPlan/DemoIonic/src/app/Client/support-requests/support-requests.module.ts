import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { SupportRequestsPageRoutingModule } from './support-requests-routing.module';

import { SupportRequestsPage } from './support-requests.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    SupportRequestsPageRoutingModule
  ],
  declarations: [SupportRequestsPage]
})
export class SupportRequestsPageModule {}

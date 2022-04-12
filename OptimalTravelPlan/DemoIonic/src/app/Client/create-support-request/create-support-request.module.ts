import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { CreateSupportRequestPageRoutingModule } from './create-support-request-routing.module';

import { CreateSupportRequestPage } from './create-support-request.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    CreateSupportRequestPageRoutingModule
  ],
  declarations: [CreateSupportRequestPage]
})
export class CreateSupportRequestPageModule {}

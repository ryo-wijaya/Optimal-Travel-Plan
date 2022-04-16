import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {AccordionModule} from 'primeng/accordion';

import { IonicModule } from '@ionic/angular';

import { FAQPageRoutingModule } from './faq-routing.module';

import { FAQPage } from './faq.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    FAQPageRoutingModule,
    AccordionModule
  ],
  declarations: [FAQPage]
})
export class FAQPageModule {}

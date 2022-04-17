import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {DropdownModule} from 'primeng/dropdown';
import { IonicModule } from '@ionic/angular';
import {MultiSelectModule} from 'primeng/multiselect';
import {TableModule} from 'primeng/table';

import { ServicesPageRoutingModule } from './services-routing.module';

import { ServicesPage } from './services.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    ServicesPageRoutingModule,
    DropdownModule,
    MultiSelectModule,
    TableModule
  ],
  declarations: [ServicesPage]
})
export class ServicesPageModule {}

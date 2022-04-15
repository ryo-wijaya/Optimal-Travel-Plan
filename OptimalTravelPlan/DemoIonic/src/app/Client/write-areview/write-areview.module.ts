import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {RatingModule} from 'primeng/rating';


import { IonicModule } from '@ionic/angular';

import { WriteAReviewPageRoutingModule } from './write-areview-routing.module';

import { WriteAReviewPage } from './write-areview.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    WriteAReviewPageRoutingModule,
    RatingModule
  ],
  declarations: [WriteAReviewPage]
})
export class WriteAReviewPageModule {}

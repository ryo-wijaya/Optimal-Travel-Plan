import { Component, OnInit } from '@angular/core';

import { NavParams } from '@ionic/angular';
import { ModalController } from '@ionic/angular'
import { Booking } from '../models/booking';
import { Service } from '../models/service';

@Component({
  selector: 'app-create-new-booking',
  templateUrl: './create-new-booking.page.html',
  styleUrls: ['./create-new-booking.page.scss'],
})
export class CreateNewBookingPage implements OnInit {

  name: string;
  startDate: Date;
  endDate: Date;
  message: string;
  error: boolean;

  constructor(public navParams: NavParams,
    public modalController: ModalController) {
    this.error = false;
    this.name = navParams.get('value');
    let start = navParams.get('start');
    let end = navParams.get('end');

    // console.log("start = " + start);
    // if (start != null && start != 'null') {
    //   this.startDate = start;
    // }
    // if(end != null && start != 'null'){
    //   this.endDate =
    // }

  }

  ngOnInit() {
  }

  public printLog() {
    console.log(this.startDate);
    console.log(this.endDate);
    console.log(this.endDate > this.startDate);
  }


  public closeModal() {
    if (this.startDate && this.endDate && this.startDate < this.endDate) {
      this.modalController.dismiss({
        'start': this.startDate,
        'end': this.endDate
      });
    } else if (!this.startDate || !this.endDate) {
      this.error = true;
      this.message = "Please select both dates!";
    } else {
      this.error = true;
      this.message = "End date must be after start date!";
    }
  }

  returnButton() {
    this.modalController.dismiss({
    });
  }

}

import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AlertController } from '@ionic/angular';
import { Booking } from 'src/app/models/booking';
import { Customer } from 'src/app/models/customer';
import { SupportRequest } from 'src/app/models/support-request';
import { TravelItinerary } from 'src/app/models/travel-itinerary';
import { SupportRequestService } from 'src/app/services/support-request.service';

@Component({
  selector: 'app-create-support-request',
  templateUrl: './create-support-request.page.html',
  styleUrls: ['./create-support-request.page.scss'],
})
export class CreateSupportRequestPage implements OnInit {

  details: string;
  submitted: boolean;
  customer: Customer;
  password: string;
  travelItinerary: TravelItinerary;
  selectedBooking: Booking;
  successful: boolean;
  fail: boolean;
  message: string;

  constructor(private router: Router,
    private supportRequestService: SupportRequestService,
    public alertController: AlertController) {
    this.successful = false;
    this.fail = false;
    this.submitted = false;
    this.travelItinerary = new TravelItinerary();
  }

  ngOnInit() {
    this.customer = JSON.parse(sessionStorage['customer']);
    this.password = sessionStorage['password'];

    let temp = sessionStorage['travelItinerary'];
    if (temp != null) {
      this.travelItinerary = JSON.parse(temp);
    }
  }

  createRequest(supportRequestForm: NgForm) {
    this.submitted = true;

    if (supportRequestForm.valid) {
      this.supportRequestService.createSupportRequest(this.customer.username, this.password, this.details, this.selectedBooking.bookingId).subscribe({
        next: (response) => {
          sessionStorage['customer'] = JSON.stringify(this.customer);
          sessionStorage['password'] = this.password;
          this.successful = true;
          this.message = "Support request created with Id " + (response);
        },
        error: (error) => {
          this.fail = true;
          this.message = "Booking already has a pending support request";
        }
      });
    }
  }
}

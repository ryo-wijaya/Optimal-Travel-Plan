import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ModalController } from '@ionic/angular';
import { Booking } from '../models/booking';
import { Customer } from '../models/customer';
import { BookingService } from '../services/booking.service';
import { CreateNewBookingPage } from '../create-new-booking/create-new-booking.page';
import { TravelItinerary } from '../models/travel-itinerary';
import { BookingHandler } from '../models/booking-handler';

@Component({
  selector: 'app-view-booking-details',
  templateUrl: './view-booking-details.page.html',
  styleUrls: ['./view-booking-details.page.scss'],
})
export class ViewBookingDetailsPage implements OnInit {

  booking: Booking;
  customer: Customer;
  private password: string;
  retrieveBookingError: boolean;
  message: string;
  cost: number;
  errorMsg: string;

  constructor(private activatedRoute: ActivatedRoute,
    private bookingService: BookingService,
    private router: Router,
    public modalController: ModalController) {
    this.retrieveBookingError = false;
  }

  ngOnInit() {
    let bookingID: number = parseInt(this.activatedRoute.snapshot.paramMap.get('bookingId'));
    let tempCus = sessionStorage['customer'];
    if (tempCus != null) {
      this.customer = JSON.parse(tempCus);
      this.password = sessionStorage['password'];
      
    console.log("booking details page found customer obj from session");
    }
    this.bookingService.retrieveBookingById(this.customer.username, this.password, bookingID).subscribe
      ({
        next: (response) => {
          this.booking = response.booking;
          this.cost = response.cost;
        },
        error: (error) => {
          this.retrieveBookingError = true;
          this.errorMsg = "Retrieve Booking error!";
          console.log('********** retrieve service error: ' + error);
        }
      });
  }

  public formatDate(date: Date): string {
    let output: string;
    output = date.toString().slice(0, 19);
    output = output.replace("T", " ");
    let hour = parseInt(output.slice(11, 13));
    let morning = "am";
    let hourS = hour.toString();
    if (hour > 12) {
      hour -= 12;
      morning = "pm"
    }
    if (hour < 10) {
      hourS = "0" + hour.toString();
    } else {
      hourS = hour.toString();
    }
    output = output.slice(0, 11) + hourS + output.slice(13, 16) + morning;
    return output;
  }

  async editBookingDates() {
    const modal = await this.modalController.create({
      component: CreateNewBookingPage,
      componentProps: { value: this.booking.service.serviceName }
    });
    modal.onDidDismiss().then((event) => {

      let itin = sessionStorage['travelItinerary'];
      console.log(itin);
      if (itin != null) {

        if (event.data.start != null && event.data.end != null) {
          this.booking.startDate = event.data.start;
          this.booking.endDate = event.data.end;
        } else {
          this.retrieveBookingError = true;
          this.errorMsg = "Both dates not selected!";
        }
        this.updateBooking();
      } else {
        this.errorMsg = "Error! Travel Itinerary not found!";
        this.retrieveBookingError = true;
      }
    });

    await modal.present();

  }

  public updateBooking() {

    let sessionItin: TravelItinerary;
    sessionItin = JSON.parse(sessionStorage['travelItinerary']);
    for (var i = 0; i < sessionItin.bookings.length; i++) {
      if (sessionItin.bookings[i].bookingId == this.booking.bookingId) {
        sessionItin.bookings[i] = this.booking;
        break;
      }
    }

    console.log("Updating travel init");

    sessionStorage['travelItinerary'] = JSON.stringify(sessionItin);
    let handler: BookingHandler;
    handler = new BookingHandler();
    handler.booking = this.booking;
    handler.customer = this.customer;
    handler.password = this.password;

    this.bookingService.updateBooking(handler).subscribe({
      next: (response) => {
        this.booking = response.booking;
        this.cost = response.cost;
        console.log("update successful via restful");
      },
      error: (error) => {
        this.retrieveBookingError = true;
        this.errorMsg = "Update Booking error";
      }
    });

    this.message = "Update complete!";
  }

  public deleteBooking() {
    let id: number;
    id = this.booking.bookingId;
    this.bookingService.deleteBooking(this.customer.username, this.password, this.booking.bookingId).subscribe
      ({
        next: (response) => {
          this.message = "Deleted Booking!";
          let sessionItin: TravelItinerary;
          sessionItin = JSON.parse(sessionStorage['travelItinerary']);
          sessionItin.bookings = sessionItin.bookings.filter(function(value,index,arr){
            return value.bookingId != id;
          });
          console.log(sessionItin.bookings);
          sessionStorage['travelItinerary'] = JSON.stringify(sessionItin);
          this.router.navigate(['travelItineraryDetails']);
        },
        error: (error) => {
          this.retrieveBookingError = true;
          this.errorMsg = "Delete Booking error";
          console.log('********** Delete booking error: ' + error);
        }
      });

  }

  writeAReview(){
    this.router.navigate(['/client/writeAReview/' + this.booking.bookingId]);
  }

  viewBooking(){
    this.router.navigate(['/serviceDetails/' + this.booking.service.serviceId]);
  }
}

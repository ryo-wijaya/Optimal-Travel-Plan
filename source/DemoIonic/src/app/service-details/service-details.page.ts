import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Booking } from '../models/booking';
import { Customer } from '../models/customer';
import { Service } from '../models/service';
import { TravelItinerary } from '../models/travel-itinerary';
import { ServiceService } from '../services/service.service';
import { AlertController, ModalController } from '@ionic/angular';
import { CreateNewBookingPage } from '../create-new-booking/create-new-booking.page';
import { Review } from '../models/review';
import { ReviewService } from '../services/review.service';
import { ServiceRate } from '../models/service-rate';

@Component({
  selector: 'app-service-details',
  templateUrl: './service-details.page.html',
  styleUrls: ['./service-details.page.scss'],
})
export class ServiceDetailsPage implements OnInit {

  service: Service;
  private customer: Customer;
  private password: string;
  retrieveServiceError: boolean;
  booking: Booking;
  reviews: Review[];

  constructor(private activatedRoute: ActivatedRoute,
    private serviceService: ServiceService,
    private reviewService: ReviewService,
    private router: Router,
    public modalController: ModalController,
    public alertController: AlertController) {
    this.retrieveServiceError = false;
    this.booking = new Booking();
    this.reviews = [];
  }

  ngOnInit() {
    let serID: number = parseInt(this.activatedRoute.snapshot.paramMap.get('serviceId'));
    console.log("init travel itin details page");
    let tempCus = sessionStorage['customer'];
    if (tempCus != null) {
      this.customer = JSON.parse(tempCus);
      this.password = sessionStorage['password'];
    }
    this.serviceService.retrieveAllActiveServices().subscribe({
      next: (response) => {
        let services: Service[] = response;
        for (let s of services) {
          if (s.serviceId == serID) {
            this.service = s;
          }
        }

        this.reviewService.retrieveReviewsByServiceId(this.customer.username, this.password, this.service.serviceId).subscribe({
          next: (response) => {
            this.reviews = (response);
          },
          error: (error) => {
            console.log('********** retrieve reviews for service error: ' + error);
          }
        });

      },
      error: (error) => {
        this.retrieveServiceError = true;
        console.log('********** retrieve service error: ' + error);
      }
    });
  }

  async viewReview(review: Review) {
    const alert = await this.alertController.create({
      header: 'Review Details',
      subHeader: '"' + review.content + '"',
      buttons: [
        {
          text: 'Dismiss',
          role: 'cancel',
          cssClass: 'secondary',
          handler: () => { }
        }
      ]
    });
    await alert.present();
  }

  async addToTravelItinerary() {
    const modal = await this.modalController.create({
      component: CreateNewBookingPage,
      componentProps: { value: this.service.serviceName }
    });

    modal.onDidDismiss().then((event) => {
      let itin = sessionStorage['travelItinerary'];
      if (itin != 'null' && itin != null) {
        console.log("Adding to existing travel itin!");
        let sessionItin: TravelItinerary;
        sessionItin = JSON.parse(itin);
        let sq = this.service;
        sq.bookings = [];
        this.booking = new Booking(null, event.data.start, event.data.end, null, sq);
        console.log("New Booking start .end .name " + event.data.start + " ." + event.data.end + " ." + this.booking.service.serviceName)
        if (sessionItin.bookings == null) {
          console.log("Setting bookings to new array");
          sessionItin.bookings = [];
        }
        if (sessionItin.country == null) {
          console.log("Setting session Itin to service country");
          sessionItin.country = this.service.country;
        }
        if (sessionItin.startDate == null && sessionItin.endDate == null) {
          sessionItin.startDate = this.booking.startDate;
          sessionItin.endDate = this.booking.endDate;
        }
        sessionItin.bookings.push(this.booking);
        console.log("Adding to sessionStorage");
        sessionStorage['travelItinerary'] = JSON.stringify(sessionItin);
        console.log("Attempting to route to travelItineraryDetails");
        this.router.navigate(["/travelItineraryDetails"]);
      } else {
        console.log("Creating new travel itin!");
        let sessionItin = new TravelItinerary();
        sessionItin.customer = this.customer;
        sessionItin.country = this.service.country;
        sessionItin.startDate = this.booking.startDate;
        sessionItin.endDate = this.booking.endDate;
        let sq = this.service;
        sq.bookings = [];
        this.booking = new Booking(null, event.data.start, event.data.end, null, sq);
        sessionItin.bookings = [this.booking];
        console.log("adding bookings " + JSON.stringify(sessionItin.bookings));
        sessionStorage['travelItinerary'] = JSON.stringify(sessionItin);
        console.log("Attempting to route to travelItineraryDetails");
        this.router.navigate(["/travelItineraryDetails"]);
      }
    });

    await modal.present();
  }

  getPaymentType(ser: Service) {
    let rates: ServiceRate[];
    rates = ser.rates;
    if (rates.length > 0) {
      return JSON.stringify(rates[0].chargeType);
    } else {
      return "Free of charge!";
    }
    
  }
}

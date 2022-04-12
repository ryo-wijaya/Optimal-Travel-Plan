import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AlertController, ModalController } from '@ionic/angular';
import { Booking } from '../models/booking';
import { BookingHandler } from '../models/booking-handler';
import { Customer } from '../models/customer';
import { TravelItinerary } from '../models/travel-itinerary';
import { TravelItineraryHandler } from '../models/travel-itinerary-handler';
import { BookingService } from '../services/booking.service';
import { ServiceService } from '../services/service.service';
import { TravelItineraryService } from '../services/travel-itinerary.service';
import { CreateNewBookingPage } from '../create-new-booking/create-new-booking.page';

@Component({
  selector: 'app-travel-itinerary-details',
  templateUrl: './travel-itinerary-details.page.html',
  styleUrls: ['./travel-itinerary-details.page.scss'],
})
export class TravelItineraryDetailsPage implements OnInit {

  events: any[] | null;
  options: any;
  header: any;
  errorMessage:string;

  loggedOn: boolean;
  customer: Customer;
  password: string;
  travelItinerary: TravelItinerary;

  constructor(private router: Router,
    private serviceService: ServiceService,
    private travelItineraryService: TravelItineraryService,
    private bookingService: BookingService,
    public modalController: ModalController) {
    this.loggedOn = false;
    this.events = [];
  }

  ngOnInit() {
    let tempCus = sessionStorage['customer'];
    if (tempCus != null) {
      this.customer = JSON.parse(tempCus);
      this.password = sessionStorage['password'];
      this.loggedOn = true;
    }
    this.options = {
      headerToolbar: {
        initialDate: '2019-01-01',
        left: 'prev,next today',
        center: 'title',
        right: 'dayGridMonth,timeGridWeek,timeGridDay'
      },
      editable: true,
      selectable: true,
      selectMirror: true,
      dayMaxEvents: true
    };

    let temp = sessionStorage['travelItinerary'];
    if (temp != null) {
      this.travelItinerary = JSON.parse(temp);
    }

    setTimeout(function () {
      window.dispatchEvent(new Event('resize'))
    }, 1)

    this.refreshCal();
  }

  async setDates() {
    let val: string = "Travel Itinerary"
    if (this.travelItinerary != null && this.travelItinerary.travelItineraryId != null) { val += " id: " + this.travelItinerary.travelItineraryId; }
    const modal = await this.modalController.create({
      component: CreateNewBookingPage,
      componentProps: { value: val }
    });

    modal.onDidDismiss().then((event) => {
      this.travelItinerary.startDate = event.data.start;
      this.travelItinerary.endDate = event.data.end;
      sessionStorage['travelItinerary'] = JSON.stringify(this.travelItinerary);
      console.log("updated start date " + this.travelItinerary.startDate + " end date " + this.travelItinerary.endDate);
    });
    await modal.present();
  }

  public recommendTravelItin() {
    if (this.travelItinerary.travelItineraryId == null){
      this.errorMessage = "Travel itinerary ID is has not been created! Please try again later!";
      return;
    }
    this.travelItineraryService.recommendTravelItinerary(this.customer.username, this.password, this.travelItinerary.travelItineraryId).subscribe
      ({
        next: (response) => {
          let ti: TravelItinerary = response;
          if (ti != null) {
            this.travelItinerary = ti;
            this.refreshEvents();
          }
          sessionStorage['travelItinerary'] = JSON.stringify(this.travelItinerary);
        },
        error: (error) => {
          this.errorMessage = "Error, probably no start and end date!";
          console.log('********** Failted recommend travel itin: ' + error);
        }
      });
  }

  public refreshCal() {
    if (this.travelItinerary == null) {
      this.travelItinerary = new TravelItinerary();
      return;
    } else if (this.travelItinerary.bookings == null){
      return;
    }
    if (this.loggedOn) {
      console.log("id " + this.travelItinerary.travelItineraryId);
      if (this.travelItinerary.travelItineraryId != null) {
        let handler: TravelItineraryHandler;
        handler = new TravelItineraryHandler();
        handler.customer = this.customer;
        handler.password = this.password;
        handler.newCountryId = this.travelItinerary.country.countryId;
        handler.travelItinerary = this.travelItinerary;

        this.travelItineraryService.updateTravelItinerary(handler).subscribe
          ({
            next: (response) => {
              let ti: TravelItinerary = response;
              if (ti != null) {
                this.travelItinerary = ti;
                sessionStorage['travelItinerary'] = JSON.stringify(this.travelItinerary);
                this.refreshEvents();
              }
              sessionStorage['travelItinerary'] = JSON.stringify(this.travelItinerary);
            },
            error: (error) => {
              console.log('********** Failted creating travel itin: ' + error);
            }
          });

      } else {
        let handler: TravelItineraryHandler;
        handler = new TravelItineraryHandler();
        handler.customer = this.customer;
        handler.password = this.password;
        handler.newCountryId = this.travelItinerary.country.countryId;
        handler.travelItinerary = this.travelItinerary;

        this.travelItineraryService.createTravelItinerary(handler).subscribe({
          next: (response) => {
            console.log("Travel itinerary now has ID " + response);
            this.travelItinerary.travelItineraryId = response;
            sessionStorage['travelItinerary'] = JSON.stringify(this.travelItinerary);
            this.registerBookings();
          },
          error: (error) => {
            console.log('********** Failted creating travel itin: ' + error);
          }
        });
      }
    }
    setTimeout(function () {
      window.dispatchEvent(new Event('resize'))
    }, 1)
    this.refreshEvents();
  }


  public registerBookings() {
    for (let bk of this.travelItinerary.bookings) {
      if (bk.bookingId == null) {
        let handler: BookingHandler = new BookingHandler();
        handler.booking = bk;
        handler.customer = this.customer;
        handler.serviceId = bk.service.serviceId;
        handler.travelItineraryId = this.travelItinerary.travelItineraryId;
        handler.password = this.password;
        this.bookingService.createBooking(handler).subscribe({
          next: (response) => {
            console.log("Booking now has ID " + response);
            bk.bookingId = response;
            this.refreshEvents();
          },
          error: (error) => {
            console.log('********** Create booking error : ' + error);
          }
        });
      }
    }
  }



  public refreshEvents() {
    if (this.travelItinerary.bookings != null) {
      let id = 0;
      this.events = [];
      for (let bk of this.travelItinerary.bookings) {
        id++;
        this.events.push(
          {
            'id': id,
            'start': this.formatDate(bk.startDate),
            'end': this.formatDate(bk.endDate),
            'title': bk.service.serviceName
          }
        );
        this.options = { ...this.options, ...{ events: this.events } };
      }
    }
    setTimeout(function () {
      window.dispatchEvent(new Event('resize'))
    }, 1)
  }

  public formatDate(date: Date) {
    return date.toString().slice(0, 19);
  }
}

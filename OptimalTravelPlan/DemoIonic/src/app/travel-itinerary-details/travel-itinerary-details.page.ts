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



@Component({
  selector: 'app-travel-itinerary-details',
  templateUrl: './travel-itinerary-details.page.html',
  styleUrls: ['./travel-itinerary-details.page.scss'],
})
export class TravelItineraryDetailsPage implements OnInit {

  events: any[] | null;
  options: any;
  header: any;

  loggedOn: boolean;
  customer: Customer;
  password: string;
  travelItinerary: TravelItinerary;

  constructor(private router: Router,
    private serviceService: ServiceService,
    private travelItineraryService: TravelItineraryService,
    private bookingService: BookingService) {
    this.loggedOn = false;
    this.events = [];
  }

  ngOnInit() {
    console.log("init travel itin details page");
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

    this.refreshCal();
  }

  public refreshCal() {
    if (this.travelItinerary == null) {
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
  }

  public formatDate(date: Date) {
    return date.toString().slice(0, 19);
  }
}

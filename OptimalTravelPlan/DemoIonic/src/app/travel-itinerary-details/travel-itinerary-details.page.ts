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
import { PaymentAccount } from '../models/payment-account';
import { PaymentAccountService } from '../services/payment-account.service';
import { NgIf } from '@angular/common';
import { PaymentTransaction } from '../models/payment-transaction';

@Component({
  selector: 'app-travel-itinerary-details',
  templateUrl: './travel-itinerary-details.page.html',
  styleUrls: ['./travel-itinerary-details.page.scss'],
})
export class TravelItineraryDetailsPage implements OnInit {

  events: any[] | null;
  options: any;
  header: any;
  errorMessage: string;

  loggedOn: boolean;
  customer: Customer;
  password: string;
  travelItinerary: TravelItinerary;
  paid: boolean;
  subtotal: number;

  paymentAccounts: PaymentAccount[];
  bookings: Booking[];

  constructor(private router: Router,
    private serviceService: ServiceService,
    private travelItineraryService: TravelItineraryService,
    private bookingService: BookingService,
    public modalController: ModalController,
    public alertController: AlertController,
    public paymentAccountService: PaymentAccountService) {
    this.loggedOn = false;
    this.events = [];
    this.paid = true;
    this.subtotal = 1;
  }

  ngOnInit() {
    console.log("TravelItineraryDetailsPage on init");
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


    setTimeout(function () {
      window.dispatchEvent(new Event('resize'))
    }, 1)

    this.refreshCal();
  }

  ionViewDidEnter() { this.refreshCal(); }
  servicesPage() { this.router.navigate(['/client/services']); }

  async setDates() {
    let val: string = "Travel Itinerary"
    if (this.travelItinerary != null && this.travelItinerary.travelItineraryId != null) { val += " id: " + this.travelItinerary.travelItineraryId; }
    const modal = await this.modalController.create({
      component: CreateNewBookingPage,
      componentProps: {
        value: val,
        start: this.travelItinerary.startDate,
        end: this.travelItinerary.endDate
      }
    });

    modal.onDidDismiss().then((event) => {
      this.travelItinerary.startDate = event.data.start;
      this.travelItinerary.endDate = event.data.end;
      sessionStorage['travelItinerary'] = JSON.stringify(this.travelItinerary);
      console.log("updated start date " + this.travelItinerary.startDate + " end date " + this.travelItinerary.endDate);

      this.refreshCal();
    });
    await modal.present();
  }

  public recommendTravelItin() {
    if (this.travelItinerary.travelItineraryId == null) {
      this.errorMessage = "Please start by adding one booking for country to be added!";
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
    let temp = sessionStorage['travelItinerary'];
    if (temp != 'null' && temp != null) {
      this.travelItinerary = JSON.parse(temp);
    }
    console.log(" refreshCal" + this.travelItinerary);

    if (this.travelItinerary == null) {
      console.log(" refreshCal trav is null");

      this.travelItinerary = new TravelItinerary();
      return;
    } else if (this.travelItinerary.bookings == null) {
      console.log(" refreshCal trav bookings is null");
      return;
    }
    if (this.loggedOn) {
      console.log("Travel itin " + this.travelItinerary);

      if (this.travelItinerary.travelItineraryId != null) {

        console.log("Travel itin id " + this.travelItinerary.travelItineraryId);
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
                this.registerBookings();
                this.refreshEvents();
              }
              sessionStorage['travelItinerary'] = JSON.stringify(this.travelItinerary);
            },
            error: (error) => {
              console.log('********** Failed update travel itin: ' + error.errorMessage);
            }
          });

      } else {

        console.log("Went to create travel itin " + this.travelItinerary);
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
            console.log('********** Failted creating travel itin: ' + error().message);
          }
        });
      }
    }
    this.refreshEvents();
  }

  public registerBookings() {
    console.log("registering booking");
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
    console.log("Refreshing events ");
    this.paid = true;
    if (this.travelItinerary.bookings != null) {
      let id = 0;
      this.events = [];
      this.bookings = [];
      for (let bk of this.travelItinerary.bookings) {
        if (bk.paymentTransaction == null || bk.paymentTransaction.paymentTransactionId == null) {
          this.paid = false;
        }
        id++;
        this.bookings.push(bk);
        this.events.push(
          {
            'id': id,
            'start': this.formatDate2(bk.startDate),
            'end': this.formatDate2(bk.endDate),
            'title': bk.service.serviceName,
            'url': ('http://localhost:8100/viewBookingDetails/' + bk.bookingId)
          }
        );
        this.options = { ...this.options, ...{ events: this.events } };
      }
    }
    setTimeout(function () {
      window.dispatchEvent(new Event('resize'))
    }, 1)
  }

  public formatDate2(date: Date) {
    return date.toString().slice(0, 19);
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

  public viewBookingDetails(bookingId: number) {
    this.router.navigate(['viewBookingDetails/' + bookingId]);
  }


  public makePayment() {
    this.travelItineraryService.calculateTotalItineraryPrice(this.customer.username, this.password, this.travelItinerary.travelItineraryId).subscribe({
      next: (response) => {
        this.subtotal = response;
        this.paymentAccountService.retrieveAllPaymentAccount(this.customer.username, this.password).subscribe({
          next: (response) => {
            this.paymentAccounts = response;
            if (this.paymentAccounts.length > 0) {
              this.popup();
            } else {
              this.errorMessage = "Please set up a payment account first!";
            }

            console.log("changing subtotal to new " + response)
          },
          error: (error) => {
            console.log('********** Failed to query total price: ' + error);
          }
        });
      },
      error: (error) => {
        console.log('********** Failed to query total price: ' + error);
      }
    });
  }

  async popup() {
    let arr = [];

    for (let account of this.paymentAccounts) {
      if (account.enabled) {
        console.log("Account found! " + account.accountNumber);
        arr.push({
          name: account.accountNumber,
          type: 'radio',
          label: account.accountNumber,
          value: account.accountNumber
        });
      }
    }

    const alert = await this.alertController.create({
      header: 'Total Price: $' + this.subtotal,
      inputs: arr,
      buttons: [{
        text: 'Confirm',
        role: 'ok',
        cssClass: 'secondary',
        handler: (val) => {
          this.confirmPayment(val);
        }
      },
      { text: 'Back', role: 'cancel', cssClass: 'secondary' }]
    });
    await alert.present();
  }

  private confirmPayment(val) {

    this.travelItineraryService.payForAllBookings(this.customer.username, this.password, val, this.travelItinerary.travelItineraryId).subscribe({
      next: (response) => {
        console.log("Payment successful " + response);
        this.subtotal = 0;
        this.travelItinerary = response;
        sessionStorage['travelItinerary'] = JSON.stringify(this.travelItinerary);
      },
      error: (error) => {
        console.log('********** Failted creating travel itin: ' + error);
      }
    });
  }
}

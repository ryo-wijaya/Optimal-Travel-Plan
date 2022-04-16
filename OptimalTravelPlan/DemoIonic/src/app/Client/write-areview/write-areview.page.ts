import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Booking } from 'src/app/models/booking';
import { Customer } from 'src/app/models/customer';
import { Review } from 'src/app/models/review';
import { ReviewHandler } from 'src/app/models/review-handler';
import { TravelItinerary } from 'src/app/models/travel-itinerary';
import { BookingService } from 'src/app/services/booking.service';
import { ReviewService } from 'src/app/services/review.service';

@Component({
  selector: 'app-write-areview',
  templateUrl: './write-areview.page.html',
  styleUrls: ['./write-areview.page.scss'],
})
export class WriteAReviewPage implements OnInit {

  booking: Booking;
  customer: Customer;
  private password: string;
  message: string;
  errorMsg: string;

  val2: number;
  review: string;


  constructor(private activatedRoute: ActivatedRoute,
    private bookingService: BookingService,
    private router: Router,
    private reviewService: ReviewService) { }

  ngOnInit() {
    let bookingID: number = parseInt(this.activatedRoute.snapshot.paramMap.get('bookingId'));
    let tempCus = sessionStorage['customer'];
    if (tempCus != null) {
      this.customer = JSON.parse(tempCus);
      this.password = sessionStorage['password'];
    }
    this.bookingService.retrieveBookingById(this.customer.username, this.password, bookingID).subscribe
      ({
        next: (response) => {
          this.booking = response.booking;
          if (this.booking.review != null && this.booking.review.rating != null) {
            this.val2 = this.booking.review.rating;
            this.review = this.booking.review.content;
          }
        },
        error: (error) => {
          this.errorMsg = "Retrieve Booking error!";
          console.log('********** retrieve service error: ' + error);
        }
      });
  }

  submit() {
    console.log("Submit called! booking id = " + this.booking.bookingId + " | ");

    if (this.val2 == null || this.review == null || this.review.length < 1) {
      this.errorMsg = "Please ensure all fields are filled in!";
    } else if (this.booking.review != null && this.booking.review.reviewId != null) {
      console.log("Booking review exist, updating! id = " + this.booking.review.reviewId);
      this.booking.review.rating = this.val2;
      this.booking.review.content = this.review;

      let handler: ReviewHandler;
      handler = new ReviewHandler();
      handler.bookingId = this.booking.bookingId;
      handler.review = this.booking.review;
      handler.customer = this.customer;
      handler.password = this.password;
      handler.reviewId = this.booking.review.reviewId;
      this.reviewService.updateReview(handler).subscribe({
        next: (response) => {
          this.booking.review = response;
          let itin = sessionStorage['travelItinerary'];
          if (itin != null && itin != 'null') {
            itin = JSON.parse(itin);
            for (let i = 0; i < itin.bookings.length; i++) {
              if (itin.bookings[i].bookingId == this.booking.bookingId) {
                itin.bookings[i] = this.booking;
                sessionStorage['travelItinerary'] = JSON.stringify(itin);
                this.errorMsg = null;
                break;
              }
            }
          }
          this.message = "Successfully Updated!";
          this.errorMsg = null;
        },
        error: (error) => {
          this.errorMsg = "Create new review error!";
          this.message = null;
          console.log('********** retrieve service error: ' + error);
        }
      });

    } else {
      console.log("Booking review is being created!");
      this.booking.review = new Review();

      this.booking.review.rating = this.val2;
      this.booking.review.content = this.review;

      let handler: ReviewHandler;
      handler = new ReviewHandler();
      handler.bookingId = this.booking.bookingId;
      handler.review = this.booking.review;
      handler.customer = this.customer;
      handler.password = this.password;
      this.reviewService.createReview(handler).subscribe({
        next: (response) => {
          this.booking.review.reviewId = response;
          let itin = sessionStorage['travelItinerary'];
          if (itin != null && itin != 'null') {
            itin = JSON.parse(itin);
            for (let i = 0; i < itin.bookings.length; i++) {
              if (itin.bookings[i].bookingId == this.booking.bookingId) {
                itin.bookings[i] = this.booking;
                sessionStorage['travelItinerary'] = JSON.stringify(itin);
                break;
              }
            }
          }

          this.message = "Successfully added!";
          this.errorMsg = null;
        },
        error: (error) => {
          this.errorMsg = "Create new review error!";
          console.log('********** retrieve service error: ' + error);
        }
      });
    }
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

}

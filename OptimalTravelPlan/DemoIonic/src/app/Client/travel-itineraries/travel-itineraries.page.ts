
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AlertController } from '@ionic/angular';
import { Customer } from 'src/app/models/customer';
import { TravelItinerary } from 'src/app/models/travel-itinerary';
import { TravelItineraryService } from 'src/app/services/travel-itinerary.service';

@Component({
  selector: 'app-travel-itineraries',
  templateUrl: './travel-itineraries.page.html',
  styleUrls: ['./travel-itineraries.page.scss'],
})
export class TravelItinerariesPage implements OnInit {

  customer: Customer;
  password: string;
  itineraries: TravelItinerary[];

  constructor(private router: Router,
    private travelItineraryService: TravelItineraryService,
    public alertController: AlertController) { }

  ngOnInit() {
    this.customer = JSON.parse(sessionStorage['customer']);
    this.password = sessionStorage['password'];
    this.travelItineraryService.retrieveAllTravelItinerary(this.customer.username, this.password).subscribe({
      next: (response) => {
        this.itineraries = (response);
      },
      error: (error) => {
        console.log('********** get customer itineraries error: ' + error);
      }
    });
  }

  async viewItiDetails(event, iti: TravelItinerary) {
    if (iti == null) {
      sessionStorage['travelItinerary'] = null;
      this.router.navigate(["travelItineraryDetails/"]);
    } else {
      const alert = await this.alertController.create({
        header: 'Select an Action',

        buttons: [
          {
            text: 'Create Support Request',
            role: 'ok',
            cssClass: 'secondary',
            handler: () => {
              console.log("attempting to create support request for itinerary = " + iti.travelItineraryId);
              sessionStorage['travelItinerary'] = JSON.stringify(iti);
              this.router.navigate(["createSupportRequest/"]);
            }
          },
          {
            text: 'Go to Calendar',
            role: 'ok',
            cssClass: 'secondary',
            handler: () => {
              console.log("attempting to view travelItinerary = " + iti.travelItineraryId);
              sessionStorage['travelItinerary'] = JSON.stringify(iti);
              this.router.navigate(["travelItineraryDetails/"]);
            }
          }
        ]
      });

      await alert.present();
    }
  }


  public formatDate(date: Date): string {
    if (date == null){
      return "No date defined!";
    }
    let output: string;
    output = JSON.stringify(date).slice(0, 20);
    output = output.replace("T", " ");

    console.log(output);
    let hour = parseInt(output.slice(11, 14));
    console.log(output.slice(11, 14));
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
    output = output.slice(0, 12) + hourS + output.slice(14, 17) + morning;
    return output;
  }
}





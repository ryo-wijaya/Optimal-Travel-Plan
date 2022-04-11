import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Country } from 'src/app/models/country';
import { Customer } from 'src/app/models/customer';
import { Service } from 'src/app/models/service';
import { ServiceService } from 'src/app/services/service.service';
import { TravelItineraryService } from 'src/app/services/travel-itinerary.service';

@Component({
  selector: 'app-services',
  templateUrl: './services.page.html',
  styleUrls: ['./services.page.scss'],
})
export class ServicesPage implements OnInit {

  services: Service[];
  filteredServices: Service[];
  selectedService: Service;
  country: Country;
  customer: Customer;
  countries:Country[];

  constructor(private router: Router,
    private serviceService: ServiceService,
    private travelItineraryService:TravelItineraryService) { }

  ngOnInit() {
    this.customer = JSON.parse(sessionStorage['customer']);
    let password: string = sessionStorage['password'];
    this.serviceService.retrieveAllActiveServices(this.customer.username, password).subscribe({
      next: (response) => {
        this.services = response;
        this.filteredServices = response;
      },
      error: (error) => {
        console.log('********** retrieve service error: ' + error);
      }
    });
    this.travelItineraryService.retrieveAllCountrys().subscribe({
      next: (response) => {
        console.log(response);
        this.countries = response;
      },
      error: (error) => {
        console.log('********** retrieve country: ' + error);
      }
    });
  }

  filterByCountry(){
    if(this.country != null){
      this.filteredServices = [];
      for(let s of this.services){
        if (s.country == this.country){
          this.filteredServices.concat(s);
        }
      }
    }
  }

  viewServiceDetails(event, service:Service)
  {
    console.log("attempting to view serivce ID = " + service.serviceId);
    this.router.navigate(["/client/serviceDetail/" + service.serviceId]);
  }

}

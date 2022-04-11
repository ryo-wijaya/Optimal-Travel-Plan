import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Country } from 'src/app/models/country';
import { Customer } from 'src/app/models/customer';
import { Service } from 'src/app/models/service';
import { Tag } from 'src/app/models/tag';
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
  countries: Country[];
  tags: Tag[];
  selTag: Tag[];

  constructor(private router: Router,
    private serviceService: ServiceService,
    private travelItineraryService: TravelItineraryService) { }

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
        this.countries = [{ countryId: null, name: null, services: [] }];
        this.countries = this.countries.concat(response);
      },
      error: (error) => {
        console.log('********** retrieve country: ' + error);
      }
    });
    this.travelItineraryService.retrieveAllTags().subscribe({
      next: (response) => {
        this.tags = (response);
      },
      error: (error) => {
        console.log('********** retrieve country: ' + error);
      }
    });

  }

  filter() {
    if (this.country != null && this.country.name != null) {
      this.filteredServices = this.services
      for (let s of this.services) {
        if (s.country == this.country) {
          this.filteredServices.concat(s);
          break;
        }
        for (let t of this.selTag) {
          for(let t2 of s.tags){
            if(t.tagId == t2.tagId){
              this.filteredServices.concat(s);
              break;
            }
          }
        }
      }
    }
  }

  viewServiceDetails(event, service: Service) {
    console.log("attempting to view serivce ID = " + service.serviceId);
    this.router.navigate(["/client/serviceDetail/" + service.serviceId]);
  }

}

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
  private password:string;

  constructor(private router: Router,
    private serviceService: ServiceService,
    private travelItineraryService: TravelItineraryService) { }

  ngOnInit() {
    console.log("init travel itin details page");
    let tempCus = sessionStorage['customer'];
    if (tempCus != null) {
      this.customer = JSON.parse(tempCus);
      this.password = sessionStorage['password'];
    }
    let password: string = sessionStorage['password'];
    this.serviceService.retrieveAllActiveServices().subscribe({
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
        this.countries = [{ countryId: null, name: "No filter", services: [] }];
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
    let filter = false;
    this.filteredServices = [];
    for (let s of this.services) {
      let willAdd: boolean = false;

      if (this.country != null && this.country.name != null) {
        filter = true;
        if (s.country.countryId == this.country.countryId) {
          willAdd = true;
        }
      }

      if (!willAdd && this.selTag != null) {
        for (let t of this.selTag) {
          if (t != null && t.name != null) {
            filter = true;
            console.log(s.tags);
            for (let t2 of s.tags) {
              if (t.tagId == t2.tagId) {
                willAdd = true;
                break;
              }
            }
          }
        }
      }
      if (willAdd) {
        this.filteredServices.push(s);
      }
    }

    if (!filter) {
      this.filteredServices = this.services;
    }
  }

  viewServiceDetails(event, service: Service) {
    console.log("attempting to view serivce ID = " + service.serviceId);
    this.router.navigate(["/serviceDetails/" + service.serviceId]);
  }
}

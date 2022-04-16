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
  private password: string;
  filterString: string;

  constructor(private router: Router,
    private serviceService: ServiceService,
    private travelItineraryService: TravelItineraryService) { this.filterString = ""; }

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
    this.filteredServices = [];
    let temp:Service[] = [];
    let temp2:Service[] = [];
    if (this.country != null && this.country.name != "No filter") {
      console.log(JSON.stringify(this.country.name));

      for (let s of this.services) {
        if (s.country.countryId == this.country.countryId) {
          temp.push(s);
        }
      }
    } else {
      console.log("no country selected");
      temp = this.services;
    }
    if (this.selTag != null && this.selTag.length > 0) {

      for (let s of temp) {
        let toAdd: boolean = false;
        for (let t of this.selTag) {
          for (let t2 of s.tags) {
            if (t.tagId == t2.tagId) {
              toAdd = true;
            }
          }
        }
        if (toAdd) {
          temp2.push(s);
        }
      }
    } else {
      console.log("no tags selected");
      temp2 = temp;
    }
    if (this.filterString != null && this.filterString.length > 0) {

      console.log(this.filterString);
      for (let s of temp2) {
        if(s.serviceName.toLocaleLowerCase().includes(this.filterString.toLocaleLowerCase())){
          this.filteredServices.push(s);
        }
      }
    } else {
      console.log("no filter name");
      this.filteredServices = temp2;
    }
  }




  viewServiceDetails(event, service: Service) {
    console.log("attempting to view serivce ID = " + service.serviceId);
    this.router.navigate(["/serviceDetails/" + service.serviceId]);
  }
}

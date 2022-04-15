import { Component, OnInit } from '@angular/core';

import { Router } from '@angular/router';

import { TravelItineraryService } from 'src/app/services/travel-itinerary.service';
import { Country } from 'src/app/models/country';
import { Tag } from 'src/app/models/tag';
import { TravelItinerary } from 'src/app/models/travel-itinerary';
import { Customer } from 'src/app/models/customer';

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss'],
})
export class HomePage implements OnInit {

  countries: Country[];
  tags: Tag[];
  travelItineraries: TravelItinerary[];
  customer: Customer;
  password: string;
  refreshPage: string;

  constructor(private router: Router,
    private travelItineraryService: TravelItineraryService) { }

  ngOnInit() {
    this.refreshPage = sessionStorage['justLoggedIn'];
    if (this.refreshPage == 'true') {
      sessionStorage.removeItem('justLoggedIn');
      window.location.reload();
    }
    this.customer = JSON.parse(sessionStorage['customer']);
    this.password = sessionStorage['password'];
    this.refreshCountries();
    this.refreshTags();
    this.refreshTravelItineraries();
  }

  refreshCountries() {
    this.travelItineraryService.retrieveAllCountrys().subscribe({
      next:(response)=>{
        this.countries = response;
      },
      error:(error)=>{
        console.log('***************** view all countries ' + error);
      }
    })
  }

  refreshTags() {
    this.travelItineraryService.retrieveAllTags().subscribe({
      next:(response)=>{
        this.tags = response;
      },
      error:(error)=>{
        console.log('***************** view all Tags ' + error);
      }
    })
  }

  
  refreshTravelItineraries() {
    this.travelItineraryService.retrieveAllTravelItinerary(this.customer.username, this.password).subscribe({
      next:(response)=>{
        this.travelItineraries = response;
      },
      error:(error)=>{
        console.log('***************** view all Travel Itinerary ' + error);
      }
    })
  }
  
  
}

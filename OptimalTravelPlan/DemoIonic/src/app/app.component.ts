import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Customer } from './models/customer';
@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent {

  public userPages = [
    { title: 'Login', url: '/login', icon: 'home' },
    { title: 'Register', url: '/registration', icon: 'clipboard' }
  ];

  public customerPages = [
    { title: 'My Account', url: '/client/accountDetails', icon: 'apps' },
    { title: 'My Travel Itineraries', url: '/client/travelItineraries', icon: 'apps' },
    { title: 'My Support Requests', url: '/client/supportRequests', icon: 'apps' }
  ];

  public appPages = [
    { title: 'Travel Itinerary Detail', url: 'travelItineraryDetails', icon: 'apps' },
    { title: 'View services', url: '/client/services', icon: 'apps' }
  ]

  constructor(private router:Router) { }

  public logout(){
    sessionStorage.clear();
    this.router.navigate(['/login']);
  }

  public getIsLogin(): boolean {
    let customer: null | string;
    customer = sessionStorage['customer'];
    if (customer != null) {
      return true;
    } else {
      return false;
    }
  }
}

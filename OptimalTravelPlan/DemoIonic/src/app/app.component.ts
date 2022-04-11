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
    { title: 'Register', url: '/registration', icon: 'clipboard-outline' }
  ];

  public customerPages = [
    { title: 'My Account', url: '/client/accountDetails', icon: 'apps-outline' },
    { title: 'My Travel Itineraries', url: '/client/travelItineraries', icon: 'apps-outline' },
    { title: 'My Support Requests', url: '/client/supportRequests', icon: 'apps-outline' }
  ];

  public appPages = [
    { title: 'Make a Travel Itinerary', url: 'travelItineraryDetails', icon: 'apps-outline' },
    { title: 'View services', url: '/client/accountDetails', icon: 'apps-outline' }
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

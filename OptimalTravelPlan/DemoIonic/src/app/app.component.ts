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
    { title: 'Homepage', url: '/client/home', icon: 'home'},
    { title: 'My Account', url: '/client/accountDetails', icon: 'body' },
    { title: 'My Travel Itineraries', url: '/client/travelItineraries', icon: 'folder' },
    { title: 'My Support Requests', url: '/client/supportRequests', icon: 'alert-circle' },
    { title: 'Manage Payment', url: '/payment', icon: 'card' }
  ];

  public appPages = [
    { title: 'Travel Itinerary Detail', url: 'travelItineraryDetails', icon: 'airplane' },
    { title: 'View services', url: '/client/services', icon: 'people-circle' },
    { title: 'FAQ', url: '/faq', icon: 'alert-circle-outline'}
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

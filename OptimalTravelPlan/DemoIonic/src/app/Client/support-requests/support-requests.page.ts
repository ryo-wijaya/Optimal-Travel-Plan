import { Component, OnInit } from '@angular/core';

import { Router } from '@angular/router';

import { SupportRequest } from 'src/app/models/support-request';
import { SupportRequestService } from 'src/app/services/support-request.service';
import { Customer } from 'src/app/models/customer';

@Component({
  selector: 'app-support-requests',
  templateUrl: './support-requests.page.html',
  styleUrls: ['./support-requests.page.scss'],
})
export class SupportRequestsPage implements OnInit {

  supportRequests: SupportRequest[];
  customer: Customer;
  password: string;

  constructor(private router: Router,
    private supportRequestService: SupportRequestService) { }

  ngOnInit() {
    this.customer = JSON.parse(sessionStorage['customer']);
    this.password = sessionStorage['password'];
    this.refreshSupportRequests();
  }

  ionViewWillEnter() {
    this.refreshSupportRequests();
  }

  viewSupportRequestDetails(event, supportRequest) {
    sessionStorage['supportRequestId'] = supportRequest.supportRequestId;
    this.router.navigate(["/client/view-support-requests"])
  }

  refreshSupportRequests() {
    this.supportRequestService.retrieveSupportRequest(this.customer.username,
      this.password).subscribe({
        next:(response)=>{
          this.supportRequests = response;
        },
        error:(error)=>{
          console.log('***************** view support request ' + error);
        }
      })
  }

}

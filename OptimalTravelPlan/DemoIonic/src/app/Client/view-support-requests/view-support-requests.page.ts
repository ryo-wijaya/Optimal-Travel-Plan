import { Component, OnInit } from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';

import { AlertController } from '@ionic/angular';

import { SupportRequest } from 'src/app/models/support-request';
import { SupportRequestService } from 'src/app/services/support-request.service';
import { Customer } from 'src/app/models/customer';

@Component({
  selector: 'app-view-support-requests',
  templateUrl: './view-support-requests.page.html',
  styleUrls: ['./view-support-requests.page.scss'],
})
export class ViewSupportRequestsPage implements OnInit {

  customer: Customer;
  password: string;

  supportRequestId: number;
  supportRequestToView: SupportRequest;
  addRequestDetails: string;

  retrieveSupportRequestError: boolean;
  error: boolean;
  errorMessage: string;
  resultSuccess: boolean;
  resultError: boolean;

  constructor(private router: Router,
    private activatedRoute: ActivatedRoute,
    private supportRequestService: SupportRequestService,
    public alertController: AlertController) {
    this.retrieveSupportRequestError = false;
    this.error = false;
    this.resultSuccess = false;
    this.resultError = false;
  }

  ngOnInit() {
    this.customer = JSON.parse(sessionStorage['customer']);
    this.password = sessionStorage['password'];
    this.supportRequestId = sessionStorage['supportRequestId'];

    this.refreshSupportRequest();
  }

  ionViewWillEnter() {
    this.refreshSupportRequest();
  }

  refreshSupportRequest() {
    this.supportRequestService.retrieveSupportRequestById(this.customer.username, this.password, this.supportRequestId).subscribe({
      next: (response) => {
        this.supportRequestToView = response;
      },
      error: (error) => {
        this.retrieveSupportRequestError = true;
        console.log('************** View Support Request Page: ' + error);
      }
    });
  }

  public formatDate(date: Date): string {
    let output: string;
    output = date.toString().slice(0, 19);
    output = output.replace("T", " ");
    let hour = parseInt(output.slice(11, 13));
    let morning = "am";
    let hourS = hour.toString();
    if (hour > 12) {
      hour -= 12;
      morning = "pm"
    }
    if(hour <10){
      hourS = "0" + hour.toString();
    } else{
      hourS = hour.toString();
    }
    output = output.slice(0, 11) + hourS + output.slice(13,16) + morning;
    return output;
  }

  /*
  updateSupportRequest() {
    this.supportRequestService.updateRequestDetails(this.customer.username, this.password, this.supportRequestIdm, this.addRequestDetails).subscribe({
      next: (response)=>{
        this.
      }
    })
  }
  */

}

import { Component, OnInit } from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';
import { NgForm } from '@angular/forms';

import { Customer } from '../models/customer';
import { AccountService } from '../services/account.service';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.page.html',
  styleUrls: ['./registration.page.scss'],
})
export class RegistrationPage implements OnInit {

  submitted: boolean;
  newCustomer: Customer;

  username: string;
  password: string;
  name: string;
  mobile: string;
  passportNumber: string;
  email: string;
  vaccinationStatus: boolean;

  resultSuccess: boolean;
  resultError: boolean;
  message: string;

  constructor(private router: Router,
    private activatedRoute: ActivatedRoute,
    private accountService: AccountService) {
      this.submitted = false;
      this.newCustomer = new Customer();

      this.resultError = false;
      this.resultSuccess = false;
    }

  ngOnInit() {
  }

  clear() {
    this.submitted = false;
    this.newCustomer = new Customer();
  }

  create(createCustomerForm: NgForm) {
    this.submitted = true;
    if(createCustomerForm.valid) {
      this.accountService.createNewCustomer(this.newCustomer.username, this.newCustomer.password, this.newCustomer.name,
        this.newCustomer.mobile, this.newCustomer.passportNumber, this.newCustomer.email, this.newCustomer.vaccinationStatus).subscribe({
          next:(response)=>{
            let newCustomerId: number = response;
            this.resultSuccess = true;
            this.resultError = false;
            this.message = "Customer " + newCustomerId + " created successfully! You may attempt to log in now!";


            this.newCustomer = new Customer();
            this.submitted = true;
            createCustomerForm.reset();
            sessionStorage['fromRegistration'] = this.message;
            this.router.navigate(['/login']);
          },
          error:(error) => {
            this.resultError = true;
            this.resultSuccess = false;
            this.message = "An error has occurred whil creating the new Customer " + error;

            console.log('************ CreateNewCustomerPage: ' + error);
          }
        })
    }
  }

}

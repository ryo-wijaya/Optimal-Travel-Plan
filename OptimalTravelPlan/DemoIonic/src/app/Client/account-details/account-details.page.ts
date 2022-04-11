import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { Customer } from 'src/app/models/customer';
import { AccountService } from 'src/app/services/account.service';

@Component({
  selector: 'app-account-details',
  templateUrl: './account-details.page.html',
  styleUrls: ['./account-details.page.scss'],
})
export class AccountDetailsPage implements OnInit {

  submitted: boolean;

  customer: Customer;
  name: string;
  mobile: string;
  passportNumber: string;
  email: string;
  vaccinationStatus: boolean;
  password: string;
  username: string;

  resultSuccess: boolean;
  resultError: boolean;
  message: string;

  constructor(private router: Router,
    private accountService: AccountService) {
      this.submitted = false;
      this.resultError = false;
      this.resultSuccess = false;
    }

  ngOnInit() {
    // Had to do it this way coz of restful reasons
    this.customer = JSON.parse(sessionStorage['customer']);
    this.password = sessionStorage['password'];
    this.name = this.customer.name;
    this.mobile = this.customer.mobile;
    this.passportNumber = this.customer.passportNumber;
    this.email = this.customer.email;
    this.vaccinationStatus = this.customer.vaccinationStatus;
    this.username = this.customer.username;
  }

  updateDetails(accountDetailsForm: NgForm) {
    this.submitted = true;

    if (accountDetailsForm.valid) {
      this.accountService.updateCustomer(this.username, this.password, this.name, this.mobile, this.passportNumber, this.email,
        this.vaccinationStatus).subscribe({
        next: (response) => {
          this.message = "Profile successfully updated!";

          this.resultSuccess = true;
          this.resultError = false;
          this.customer.name = this.name;
          this.customer.mobile = this.mobile;
          this.customer.passportNumber = this.passportNumber;
          this.customer.email = this.email;
          this.customer.vaccinationStatus = this.vaccinationStatus;

          sessionStorage['customer'] = JSON.stringify(this.customer);
        },
        error: (error) => {
          this.resultError = true;
          this.resultSuccess = false;
          this.message = "An error has occured when updating the product";
        }
      });
    }
  }
}

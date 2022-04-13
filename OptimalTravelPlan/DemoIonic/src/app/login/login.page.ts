import { Component, OnInit } from '@angular/core';

import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import { AccountService } from '../services/account.service';
import { Customer } from '../models/customer';


@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage implements OnInit {

  submitted: boolean;
  username: string;
  password: string;
  loginError: boolean;
  errorMessage: string;

  message: string;

  constructor(private router: Router,
          private accountService: AccountService) { }

  ngOnInit() {
    this.message = sessionStorage['fromRegistration'];
    sessionStorage['fromRegistration'] = null;
  }

	clear()
  {
		this.username = "";
		this.password = "";
	}


	customerLogin(customerLoginForm: NgForm) 
  {
		this.submitted = true;
    this.message = "Logging in!...";

		if (customerLoginForm.valid) 
    {
      this.accountService.customerLogin(this.username, this.password).subscribe({
        next:(response)=>{
          let customer: Customer = response;

					if (customer != null) 
          {
						sessionStorage['customer'] = JSON.stringify(customer);
            sessionStorage['password'] = this.password;
						this.loginError = false;
            this.router.navigate(['/client/home']);
					}
					else
          {
            this.message = null;
						this.loginError = true;
					}
        },
        error:(error)=>{
          this.message = null;
          this.loginError = true;
					this.errorMessage = 'Invalid login credential: Username does not exist or invalid password!'
        }
      });
		}
		else
    {
		}
	}

	back()
  {
		this.router.navigate(["/index"]);
	}
}

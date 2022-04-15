import { Component, OnInit } from '@angular/core';

import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import { AccountService } from '../services/account.service';
import { Customer } from '../models/customer';
import { AlertController } from '@ionic/angular';


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
    private accountService: AccountService,
    public alertController: AlertController) { }

  ngOnInit() {
    this.message = sessionStorage['fromRegistration'];
    sessionStorage['fromRegistration'] = null;
  }

  clear() {
    this.username = "";
    this.password = "";
  }


  customerLogin(customerLoginForm: NgForm) {
    this.submitted = true;
    this.message = "Logging in!...";

    if (customerLoginForm.valid) {
      this.accountService.customerLogin(this.username, this.password).subscribe({
        next: (response) => {
          let customer: Customer = response;

          if (customer != null) {
            sessionStorage['customer'] = JSON.stringify(customer);
            sessionStorage['password'] = this.password;
            sessionStorage['justLoggedIn'] = 'true';
            this.loginError = false;
            this.router.navigate(['/client/home']);
          }
          else {
            this.message = null;
            this.loginError = true;
          }
        },
        error: (error) => {
          this.message = null;
          this.loginError = true;
          if ("404" != error().message.slice(32, 35)) {
            this.errorMessage = 'Invalid login credential: Username does not exist or invalid password!'
          } else {
            this.errorMessage = 'Cannot reach Server! Server down!';
          }
        }
      });
    }
    else {
    }
  }

  back() {
    this.router.navigate(["/index"]);
  }

  async forgotPassword() {
    const alert = await this.alertController.create({
      header: 'Enter Email Address',
      inputs: [
        {
          name: 'email',
          type: 'text',
          placeholder: 'enteremail@gmail.com'
        },
      ],
      buttons: [
        {
          text: 'Cancel',
          role: 'cancel',
          cssClass: 'secondary',
          handler: () => {
            console.log('Confirm Cancel');
          }
        }, {
          text: 'Send',
          handler: () => {
            console.log('Confirm forget password');
            alert.onDidDismiss().then((alertData) => {

              this.accountService.forgetPasswordChange(alertData.data.values.email).subscribe({
                next: (response) => {
                  this.sendAlert("Successful");
                },
                error: (error) => {
                  this.sendAlert("Failed");
                }
              });

            })
          }
        }
      ]
    });
    await alert.present();
  }

  async sendAlert(status: string) {

    let subheader: string = "";

    if (status == "Failed") {
      subheader = "No account associated with email address!";
    }

    const alert = await this.alertController.create({
      header: 'Recovery email sending ' + status,
      subHeader: subheader,
      
      buttons: [
        {
          text: 'Dismiss',
          role: 'cancel',
          cssClass: 'secondary',
          handler: () => {}
        }
      ]
    });
    await alert.present();
  }
}

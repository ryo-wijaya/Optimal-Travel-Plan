import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AlertController } from '@ionic/angular';
import { Customer } from 'src/app/models/customer';
import { PaymentAccount } from 'src/app/models/payment-account';
import { PaymentAccountHandler } from 'src/app/models/payment-account-handler';
import { PaymentTransaction } from 'src/app/models/payment-transaction';
import { PaymentType } from 'src/app/models/PaymentType-enum';
import { PaymentAccountService } from 'src/app/services/payment-account.service';
import { PaymentTransactionService } from 'src/app/services/payment-transaction.service';

@Component({
  selector: 'app-payment',
  templateUrl: './payment.page.html',
  styleUrls: ['./payment.page.scss'],
})
export class PaymentPage implements OnInit {

  paymentAccounts: PaymentAccount[];
  paymentTransactions: PaymentTransaction[];
  submitted: boolean;
  customer: Customer;
  password: string;
  newPaymentAccount: PaymentAccount;
  selectedPaymentType: PaymentType;



  constructor(private router: Router,
    private paymentService: PaymentAccountService,
    public alertController: AlertController,
    private transactionService: PaymentTransactionService) {
    this.paymentAccounts = [];
    this.newPaymentAccount = new PaymentAccount();
    this.paymentTransactions = [];
  }

  ngOnInit() {
    this.customer = JSON.parse(sessionStorage['customer']);
    this.password = sessionStorage['password'];
    
    this.paymentService.retrieveAllPaymentAccount(this.customer.username, this.password).subscribe({
      next: (response) => {
        this.paymentAccounts = (response);
      },
      error: (error) => {
        console.log('********** get payment accounts error: ' + error);
      }
    });

    this.transactionService.retrieveAllPaymentTransaction(this.customer.username, this.password).subscribe({
      next: (response) => {
        this.paymentTransactions = (response);
      },
      error: (error) => {
        console.log('********** get payment transactions error: ' + error);
      }
    });
  }

  async deleteAccConfirm(acc: PaymentAccount) {
    const alert = await this.alertController.create({
      header: 'Confirm Deletion?',

      buttons: [
        {
          text: 'Cancel',
          role: 'cancel',
          cssClass: 'secondary',
          handler: () => { }
        },
        {
          text: 'Delete',
          role: 'ok',
          cssClass: 'secondary',
          handler: () => {
            this.removeAcc(acc);
          }
        }
      ]
    });
    await alert.present();
  }

  removeAcc(acc: PaymentAccount) {
    console.log("paymentacc ID : " + acc.paymenetAccountId);
    this.paymentService.deletePaymentAccount(this.customer.username, this.password, acc.paymenetAccountId).subscribe({
      next: (response) => {

        for (let i = 0; i < this.paymentAccounts.length; i++) {
          if (this.paymentAccounts[i].paymenetAccountId == acc.paymenetAccountId) {
            this.paymentAccounts.splice(i, 1);
            break;
          }
        }
        this.deleteAccSuccess();
      },
      error: (error) => {
        this.deleteAccFailed();
      }
    });
  }

  async addAcc(addAccForm: NgForm) {

    this.submitted = true;

    if (addAccForm.valid) {
      const alert = await this.alertController.create({
        header: 'Create a new Payment Account',
        inputs: [
          {
            name: 'CardNo',
            type: 'text',
            placeholder: 'Card Number'
          },
          {
            name: 'ccv',
            type: 'password',
            placeholder: 'cvv'
          },
          {
            name: 'expiry',
            type: 'date',
            placeholder: 'Date'
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
            text: 'Add',
            handler: () => {
              console.log('Confirm New payment account');
              alert.onDidDismiss().then((alertData) => {

                this.newPaymentAccount.accountNumber =  alertData.data.values.CardNo;
                this.newPaymentAccount.ccv =  alertData.data.values.ccv;
                this.newPaymentAccount.paymentType = this.selectedPaymentType as PaymentType;
                this.newPaymentAccount.enabled = true;

                let paymentHandler = new PaymentAccountHandler(this.newPaymentAccount, this.customer, this.password);
                var date = alertData.data.values.expiry;
                console.log(date);
                paymentHandler.date = date.getMilliseconds;

                this.paymentService.createPaymentAccount(paymentHandler).subscribe({
                  next: (response) => {
                    this.newPaymentAccount.paymenetAccountId = (response);
                    this.paymentAccounts.push(this.newPaymentAccount);
                    this.newPaymentAccount = new PaymentAccount();
                    this.addAccSuccess();
                  },
                  error: (error) => {
                    this.addAccFailed();
                  }
                });
              })
            }
          }
        ]
      });
      await alert.present();
    }
  }

  async addAccFailed() {
    const alert = await this.alertController.create({
      header: 'Invalid Payment Details!',

      buttons: [
        {
          text: 'Dismiss',
          role: 'cancel',
          cssClass: 'secondary',
          handler: () => { }
        }
      ]
    });
    await alert.present();
  }

  async addAccSuccess() {
    const alert = await this.alertController.create({
      header: 'Payment Type Added!',

      buttons: [
        {
          text: 'Dismiss',
          role: 'cancel',
          cssClass: 'secondary',
          handler: () => { }
        }
      ]
    });
    await alert.present();
  }

  async deleteAccFailed() {
    const alert = await this.alertController.create({
      header: 'Failed to Delete Payment Account!',

      buttons: [
        {
          text: 'Dismiss',
          role: 'cancel',
          cssClass: 'secondary',
          handler: () => { }
        }
      ]
    });
    await alert.present();
  }

  async deleteAccSuccess() {
    const alert = await this.alertController.create({
      header: 'Payment Account Deleted!',

      buttons: [
        {
          text: 'Dismiss',
          role: 'cancel',
          cssClass: 'secondary',
          handler: () => { }
        }
      ]
    });
    await alert.present();
  }

  async viewTrans(trans: PaymentTransaction) {
    const alert = await this.alertController.create({
      header: 'Transaction Details',


      buttons: [
        {
          text: 'Dismiss',
          role: 'cancel',
          cssClass: 'secondary',
          handler: () => { }
        }
      ]
    });
    await alert.present();
  }
}

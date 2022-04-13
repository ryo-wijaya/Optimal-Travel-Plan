import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AlertController } from '@ionic/angular';
import { Customer } from 'src/app/models/customer';
import { CustomerHandler } from 'src/app/models/customer-handler';
import { Tag } from 'src/app/models/tag';
import { AccountService } from 'src/app/services/account.service';
import { TravelItineraryService } from 'src/app/services/travel-itinerary.service';

@Component({
  selector: 'app-account-details',
  templateUrl: './account-details.page.html',
  styleUrls: ['./account-details.page.scss'],
})
export class AccountDetailsPage implements OnInit {

  submitted: boolean;
  customer: Customer;
  password: string;
  tags: Tag[];
  availableTagsLeft: Tag[];
  selectedTag: Tag;


  constructor(private router: Router,
    private accountService: AccountService,
    private travelItineraryService: TravelItineraryService,
    public alertController: AlertController) {
    this.submitted = false;
    this.availableTagsLeft = [];
  }

  ngOnInit() {
    this.customer = JSON.parse(sessionStorage['customer']);
    this.password = sessionStorage['password'];
    this.travelItineraryService.retrieveAllTags().subscribe({
      next: (response) => {
        this.tags = (response);
        this.filterTags();
      },
      error: (error) => {
        console.log('********** get tags error: ' + error);
      }
    });
  }

  removeTag(event, tag: Tag) {
    for (let i = 0; i < this.customer.favouriteTags.length; i++) {
      if (this.customer.favouriteTags[i].tagId == tag.tagId) {
        this.customer.favouriteTags.splice(i, 1);
        break;
      }
    }
    this.filterTags();
  }

  addTag(event) {
    this.customer.favouriteTags.push(this.selectedTag);
    this.filterTags();
  }

  filterTags() {
    this.availableTagsLeft = [];
    // Filters what tag is left available
    for (let i = 0; i < this.tags.length; i++) {
      let tagAlreadyIncluded: boolean = false;

      for (let j = 0; j < this.customer.favouriteTags.length; j++) {
        if (this.tags[i].tagId == this.customer.favouriteTags[j].tagId) {
          tagAlreadyIncluded = true;
          break;
        }
      }
      if (!tagAlreadyIncluded) {
        this.availableTagsLeft.push(this.tags[i]);
      }
    }
  }

  updateDetails(accountDetailsForm: NgForm) {
    this.submitted = true;

    if (accountDetailsForm.valid) {
      let customerHandler = new CustomerHandler(this.customer, this.password);
      this.accountService.updateCustomer(customerHandler).subscribe({
        next: (response) => {
          sessionStorage['customer'] = JSON.stringify(this.customer);
          sessionStorage['password'] = this.password;
          this.profileUpdateSuccessful();
        },
        error: (error) => {
          this.profileUpdateFailed();
        }
      });
    }
  }

  async changePassword(event) {
    const alert = await this.alertController.create({
      header: 'Change Password',
      inputs: [
        {
          name: 'oldPass',
          type: 'password',
          placeholder: 'Old password'
        },
        {
          name: 'newPass',
          type: 'password',
          placeholder: 'New password'
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
          text: 'Change',
          handler: () => {
            console.log('Confirm Change password');
            alert.onDidDismiss().then((alertData) => {

              if (alertData.data.values.oldPass == this.password) {
                this.accountService.changePassword(this.customer.username, this.password, alertData.data.values.newPass).subscribe({
                  next: (response) => {
                    this.password = alertData.data.values.newPass;
                    sessionStorage['password'] = this.password;
                    this.passwordChangeSuccess();
                  },
                  error: (error) => {
                    console.log('********** change password: ' + error);
                  }
                });
              } else {
                this.passwordChangeFailed();
              }
            })
          }
        }
      ]
    });
    await alert.present();
  }

  async passwordChangeFailed() {
    const alert = await this.alertController.create({
      header: 'Old password is not correct!',
      
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

  async passwordChangeSuccess() {
    const alert = await this.alertController.create({
      header: 'Password Successfully Changed!',
      
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

  async profileUpdateSuccessful() {
    const alert = await this.alertController.create({
      header: 'Profile Update Successful!',
      
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

  async profileUpdateFailed() {
    const alert = await this.alertController.create({
      header: 'Profile Update Failed!',
      
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


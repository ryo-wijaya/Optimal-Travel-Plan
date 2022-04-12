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


  resultSuccess: boolean;
  resultError: boolean;
  message: string;

  constructor(private router: Router,
    private accountService: AccountService,
    private travelItineraryService: TravelItineraryService,
    public alertController: AlertController) {
    this.submitted = false;
    this.resultError = false;
    this.resultSuccess = false;
  }

  ngOnInit() {
    this.customer = JSON.parse(sessionStorage['customer']);
    this.password = sessionStorage['password'];
    this.travelItineraryService.retrieveAllTags().subscribe({
      next: (response) => {
        this.tags = (response);
        this.filterTags(null);
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
    this.filterTags(null);
  }

  addTag(event) {
    this.customer.favouriteTags.push(this.selectedTag);
    this.filterTags(null);
  }

  filterTags(event) {
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
          this.message = "Profile successfully updated!";
          this.resultSuccess = true;
          this.resultError = false;
          sessionStorage['customer'] = JSON.stringify(this.customer);
          sessionStorage['password'] = this.password;
        },
        error: (error) => {
          this.resultError = true;
          this.resultSuccess = false;
          this.message = "An error has occured when updating profile";
        }
      });
    }
  }
}

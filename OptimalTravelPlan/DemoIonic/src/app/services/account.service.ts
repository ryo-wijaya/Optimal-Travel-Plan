import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { Customer } from '../models/customer'

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  baseUrl: string = "/api/Account";

  constructor(private httpClient: HttpClient) { }

  customerLogin(username: string, password: string): Observable<Customer> {
    return this.httpClient.get<Customer>(this.baseUrl + "/customerLogin?username=" + username + "&password=" + password).pipe(
        catchError(this.handleError)
      );
  }

  createNewCustomer(username: string, password: string, name: string, mobile: string, passportNumber: string, email: string, vaccinationStatus: boolean): Observable<number> {
    return this.httpClient.put<number>(this.baseUrl + "/createCustomerAccount?username=" + username + "&password=" + password
      + "&name=" + name + "&mobile=" + mobile + "&passportNumber=" + passportNumber + "&email" + email + "&vaccinationStatus" + vaccinationStatus, null).pipe(
        catchError(this.handleError)
      );
  }

  changePassword(username: string, password: string, newPassword: string): Observable<boolean> {
    return this.httpClient.post<boolean>(this.baseUrl + "/changePassword?username=" + username + "&password=" + password + "&newPassword=" + newPassword, null).pipe(
      catchError(this.handleError)
    );
  }

  forgetPasswordChange(username:string,email:string):Observable<boolean>{
    return this.httpClient.post<boolean>(this.baseUrl + "/changePassword?username=" + username + "&email=" + email, null).pipe(
      catchError(this.handleError)
    );
  }

  associateTagToCustomer(username: string, password: string, tagId: number): Observable<boolean> {
    return this.httpClient.post<boolean>(this.baseUrl + "/associateTagToCustomer?username=" + username + "&password=" + password + "&tagId=" + tagId, null).pipe(
      catchError(this.handleError)
    );
  }

  updateCustomer(customer: Customer): Observable<boolean> {
    return this.httpClient.post<boolean>(this.baseUrl + "/updateCustomer", customer, httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage: string = "";
    if (error.error instanceof ErrorEvent) {
      errorMessage = "An unknown error has occurred: " + error.error;
    }
    else {
      errorMessage = "A HTTP error has occurred: " + `HTTP ${error.status}: ${error.error}`;
    }
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}

import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { PaymentTransaction } from '../models/payment-transaction';


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class PaymentTransactionService {

  baseUrl: string = "/api/PaymentTransaction";

  constructor(private httpClient: HttpClient) { }

  createPaymentTransaction(username: string, password: string, bookingId: number, paymentAccountId: number): Observable<PaymentTransaction> {
    return this.httpClient.put<PaymentTransaction>(this.baseUrl + "/CreatePaymentTransaction?username=" + username + "&password=" + password
      + "&bookingId=" + bookingId + "&paymentAccountId=" + paymentAccountId, null).pipe
      (
        catchError(this.handleError)
      );
  }

  retrieveAllPaymentTransaction(username: string, password: string): Observable<PaymentTransaction[]> {
    return this.httpClient.get<PaymentTransaction[]>(this.baseUrl + "/RetrieveCustomerPaymentTransactions?username=" + username + "&password=" + password).pipe
      (
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

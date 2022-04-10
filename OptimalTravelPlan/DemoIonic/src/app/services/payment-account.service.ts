import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { PaymentAccountHandler } from '../models/payment-account-handler';
import { PaymentAccount } from '../models/payment-account';


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class PaymentAccountService {

  constructor(private httpClient: HttpClient) { }

  createPaymentAccount(objHandler: PaymentAccountHandler): Observable<number> {
    return this.httpClient.get<number>(this.baseUrl + "/createPaymentAccount?username=" + objHandler.customer.username, null).pipe
      (
        catchError(this.handleError)
      );
  }

  retrieveAllPaymentAccount(username: string, password: string): Observable<boolean> {
    return this.httpClient.put<boolean>(this.baseUrl + "/retrieveAllPaymentAccount?username=" + username + "&password=" + password, null).pipe
      (
        catchError(this.handleError)
      );
  }

  updatePaymentAccount(paymentAccount: PaymentAccount): Observable<boolean> {
    return this.httpClient.post<boolean>(this.baseUrl + "/updatePaymentAccount", paymentAccount, httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  deletePaymentAccount(username: string, password: string): Observable<boolean> {
    return this.httpClient.put<boolean>(this.baseUrl + "/deletePaymentAccount?username=" + username + "&password=" + password, httpOptions).pipe
      (
        catchError(this.handleError)
      );
  }
}

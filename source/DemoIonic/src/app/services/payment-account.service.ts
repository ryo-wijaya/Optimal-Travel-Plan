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

  baseUrl: string = "/api/PaymentAccount";

  constructor(private httpClient: HttpClient) { }

  createPaymentAccount(objHandler: PaymentAccountHandler): Observable<number> {
    return this.httpClient.put<number>(this.baseUrl + "/CreatePaymentAccount", objHandler, httpOptions).pipe
      (
        catchError(this.handleError)
      );
  }

  retrieveAllPaymentAccount(username: string, password: string): Observable<PaymentAccount[]> {
    return this.httpClient.get<PaymentAccount[]>(this.baseUrl + "/RetrieveCustomerPaymentAccounts?username=" + username + "&password=" + password).pipe
      (
        catchError(this.handleError)
      );
  }

  updatePaymentAccount(objHandler: PaymentAccountHandler): Observable<boolean> {
    return this.httpClient.post<boolean>(this.baseUrl + "/UpdatePaymentAccount", objHandler, httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  deletePaymentAccount(username: string, password: string, paymentAccountId: number): Observable<boolean> {
    return this.httpClient.delete<boolean>(this.baseUrl + "/DeletePaymentAccount?username=" + username + "&password=" + password + "&paymentAccountId=" + paymentAccountId).pipe
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

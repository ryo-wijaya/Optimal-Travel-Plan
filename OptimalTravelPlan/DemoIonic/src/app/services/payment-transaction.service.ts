import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class PaymentTransactionService {

  constructor(private httpClient: HttpClient) { }

  createPaymentTransaction(username: string, password: string, bookingId: number, paymentAccountId: number): Observable<number> {
    return this.httpClient.put<number>(this.baseUrl + "/createPaymentTransaction?username=" + username + "&password=" + password
      + "&bookingId=" + bookingId + "&paymentAccountId=" + paymentAccountId, null).pipe
      (
        catchError(this.handleError)
      );
  }

  createPaymentTransaction(username: string, password: string): Observable<boolean> {
    return this.httpClient.put<boolesn>(this.baseUrl + "/createPaymentTransaction?username=" + username + "&password=" + password, null).pipe
      (
        catchError(this.handleError)
      );
  }
}

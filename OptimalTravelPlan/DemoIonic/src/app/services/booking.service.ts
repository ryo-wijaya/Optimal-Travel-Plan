import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { BookingHandler } from '../models/booking-handler';


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class BookingService {

  constructor(private httpClient: HttpClient) { }

  createBooking(objHandler: BookingHandler): Observable<number> {
    return this.httpClient.get<number>(this.baseUrl + "/createBooking?objHandler=" + objHandler, null).pipe
      (
        catchError(this.handleError)
      );
  }

  updateCustomer(objHandler: BookingHandler): Observable<boolean> {
    return this.httpClient.post<boolean>(this.baseUrl + "/updateCustomer", objHandler, httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  deleteBooking(username: string, password: string, bookingId: number): Observable<boolean> {
    return this.httpClient.put<boolean>(this.baseUrl + "/deleteBooking?username=" + username + "&password=" + password + "&bookingId=" + bookingId, httpOptions).pipe
      (
        catchError(this.handleError)
      );
  }
}

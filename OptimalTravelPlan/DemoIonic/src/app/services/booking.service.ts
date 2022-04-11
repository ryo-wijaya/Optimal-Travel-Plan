import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { BookingHandler } from '../models/booking-handler';
import { Booking } from '../models/booking';


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class BookingService {

  baseUrl: string = "/api/Booking";

  constructor(private httpClient: HttpClient) { }

  createBooking(objHandler: BookingHandler): Observable<number> {
    return this.httpClient.put<number>(this.baseUrl + "/Create", objHandler,httpOptions).pipe
      (
        catchError(this.handleError)
      );
  }

  retrieveBookingById(username: string, password: string, bookingId: number): Observable<Booking> {
    return this.httpClient.get<Booking>(this.baseUrl + "/RetrieveBookingById/"+ bookingId +"?username=" + username + "&password=" + password).pipe
    (
      catchError(this.handleError)
    );
  }

  updateCustomer(objHandler: BookingHandler): Observable<boolean> {
    return this.httpClient.post<boolean>(this.baseUrl + "/Update", objHandler, httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  deleteBooking(username: string, password: string, bookingId: number): Observable<boolean> {
    return this.httpClient.delete<boolean>(this.baseUrl + "/Delete/"+ bookingId +"?username=" + username + "&password=" + password).pipe
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

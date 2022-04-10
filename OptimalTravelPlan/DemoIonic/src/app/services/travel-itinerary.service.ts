import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { TravelItineraryHandler } from '../models/travel-itinerary-handler';


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class TravelItineraryService {

  constructor(private httpClient: HttpClient) { }

  createTravelItinerary(objHandler: TravelItineraryHandler): Observable<number> {
    return this.httpClient.get<number>(this.baseUrl + "/createTravelItinerary?objHandler=" + objHandler, null).pipe
      (
        catchError(this.handleError)
      );
  }

  updateTravelItinerary(objHandler: TravelItineraryHandler): Observable<boolean> {
    return this.httpClient.get<boolean>(this.baseUrl + "/updateTravelItinerary?objHandler=" + objHandler, null).pipe
      (
        catchError(this.handleError)
      );
  }

  retrieveAllTravelItinerary(username: string, password: string): Observable<boolean> {
    return this.httpClient.put<boolean>(this.baseUrl + "/retrieveAllTravelItinerary?username=" + username + "&password=" + password, null).pipe
      (
        catchError(this.handleError)
      );
  }

  recommendTravelItinerary(username: string, password: string, travelItineraryId: number): Observable<boolean> {
    return this.httpClient.put<boolean>(this.baseUrl + "/recommendTravelItinerary?username=" + username + "&password=" + password + "&travelItineraryId=" + travelItineraryId, httpOptions).pipe
      (
        catchError(this.handleError)
      );
  }

  deleteTravelItinerary(username: string, password: string, travelItineraryId: number): Observable<boolean> {
    return this.httpClient.put<boolean>(this.baseUrl + "/deleteTravelItinerary?username=" + username + "&password=" + password + "&travelItineraryId=" + travelItineraryId, httpOptions).pipe
      (
        catchError(this.handleError)
      );
  }
}

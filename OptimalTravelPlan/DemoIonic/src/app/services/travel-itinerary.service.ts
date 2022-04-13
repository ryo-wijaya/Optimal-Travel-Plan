import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { TravelItineraryHandler } from '../models/travel-itinerary-handler';
import { Tag } from '../models/tag';
import { Country } from '../models/country';
import { TravelItinerary } from '../models/travel-itinerary';


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class TravelItineraryService {

  baseUrl: string = "/api/TravelItinerary";

  constructor(private httpClient: HttpClient) { }

  createTravelItinerary(objHandler: TravelItineraryHandler): Observable<number> {
    console.log("Create Travel itin service : " + objHandler.customer.username
     + " password " + objHandler.password + " country id " + objHandler.newCountryId
      + " TI " + objHandler.travelItinerary);
    return this.httpClient.put<number>(this.baseUrl + "/Create", objHandler, httpOptions).pipe
      (
        catchError(this.handleError)
      );
  }

  updateTravelItinerary(objHandler: TravelItineraryHandler): Observable<TravelItinerary> {
    
    objHandler.travelItinerary.endDate = new Date();
    objHandler.travelItinerary.startDate = new Date();

    // for (let k in objHandler.customer){
    //   console.log("k = " + k);
    // }

    console.log("Update Travel itin service : " + objHandler.customer
     + " password " + objHandler.password + " country id " + objHandler.newCountryId
      + " TI " + objHandler.travelItinerary.travelItineraryId + " start date "+ objHandler.travelItinerary.startDate +
      " end date " + objHandler.travelItinerary.endDate);
      objHandler.travelItineraryId = objHandler.travelItinerary.travelItineraryId;
      
    return this.httpClient.post<TravelItinerary>(this.baseUrl + "/Update", objHandler, httpOptions).pipe
      (
        catchError(this.handleError)
      );
  }

  retrieveAllTravelItinerary(username: string, password: string): Observable<TravelItinerary[]> {
    return this.httpClient.get<TravelItinerary[]>(this.baseUrl + "/RetrieveCustomerTravelItinerary?username=" + username + "&password=" + password).pipe
      (
        catchError(this.handleError)
      );
  }

  recommendTravelItinerary(username: string, password: string, travelItineraryId: number): Observable<TravelItinerary> {
    console.log("recommend travel itin user pass id" + username + " " + password + " " + travelItineraryId);
    return this.httpClient.post<TravelItinerary>(this.baseUrl + "/RecommendTravelItinerary/" + travelItineraryId + "?username=" + username + "&password=" + password, null).pipe
      (
        catchError(this.handleError)
      );
  }

  deleteTravelItinerary(username: string, password: string, travelItineraryId: number): Observable<boolean> {
    return this.httpClient.delete<boolean>(this.baseUrl + "/deleteTravelItinerary/" + travelItineraryId + "?username=" + username + "&password=" + password, httpOptions).pipe
      (
        catchError(this.handleError)
      );
  }

  retrieveAllTags(): Observable<Tag[]> {
    return this.httpClient.get<Tag[]>(this.baseUrl + "/RetrieveAllTags").pipe
      (
        catchError(this.handleError)
      );
  }

  retrieveAllCountrys(): Observable<Country[]> {
    return this.httpClient.get<Country[]>(this.baseUrl + "/RetrieveAllCountries").pipe
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

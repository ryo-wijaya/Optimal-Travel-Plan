import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ServiceByTagHandler } from '../models/service-by-tag-handler';
import { Service } from '../models/service';


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class ServiceService {

  baseUrl: string = "/api/Service";

  constructor(private httpClient: HttpClient) { }

  retrieveAllActiveServices(): Observable<Service[]> {
    console.log("retrieve services");
    return this.httpClient.get<Service[]>(this.baseUrl + "/retrieveAllActiveServices").pipe
      (
        catchError(this.handleError)
      );
  }

  retrieveAllActiveEntertainment(username: string, password: string): Observable<Service[]> {
    return this.httpClient.get<Service[]>(this.baseUrl + "/retrieveAllActiveEntertainment?username=" + username + "&password=" + password).pipe
      (
        catchError(this.handleError)
      );
  }

  retrieveAllActiveServiceByCountryId(username: string, password: string, countryId: number): Observable<Service[]> {
    return this.httpClient.get<Service[]>(this.baseUrl + "/retrieveAllActiveServiceByCountryId?username=" + username + "&password=" + password
      + "&countryId=" + countryId).pipe
      (
        catchError(this.handleError)
      );
  }

  retrieveAllActiveServiceByBusinessId(username: string, password: string, businessId: number): Observable<Service[]> {
    return this.httpClient.get<Service[]>(this.baseUrl + "/retrieveAllActiveServiceByBusinessId?username=" + username + "&password=" + password
      + "&businessId=" + businessId).pipe
      (
        catchError(this.handleError)
      );
  }

  retrieveAllActiveServiceByTags(dataWrapper: ServiceByTagHandler): Observable<Service[]> {
    return this.httpClient.post<Service[]>(this.baseUrl + "/retrieveAllActiveServiceByTags", dataWrapper, httpOptions).pipe(
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

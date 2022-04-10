import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ServiceByTagHandler } from '../models/service-by-tag-handler';


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class ServiceService {

  constructor(private httpClient: HttpClient) { }

  retrieveAllActiveServices(username: string, password: string): Observable<boolean> {
    return this.httpClient.put<boolean>(this.baseUrl + "/retrieveAllActiveServices?username=" + username + "&password=" + password, null).pipe
      (
        catchError(this.handleError)
      );
  }

  retrieveAllActiveEntertainment(username: string, password: string): Observable<boolean> {
    return this.httpClient.put<boolean>(this.baseUrl + "/retrieveAllActiveEntertainment?username=" + username + "&password=" + password, null).pipe
      (
        catchError(this.handleError)
      );
  }

  retrieveAllActiveServiceByCountryId(username: string, password: string, countryId: number): Observable<boolean> {
    return this.httpClient.put<boolean>(this.baseUrl + "/retrieveAllActiveServiceByCountryId?username=" + username + "&password=" + password + "&countryId=" + countryId, null).pipe
      (
        catchError(this.handleError)
      );
  }

  retrieveAllActiveServiceByBusinessId(username: string, password: string, businessId: number): Observable<boolean> {
    return this.httpClient.put<boolean>(this.baseUrl + "/retrieveAllActiveServiceByBusinessId?username=" + username + "&password=" + password + "&countryId=" + businessId, null).pipe
      (
        catchError(this.handleError)
      );
  }
  
  retrieveAllActiveServiceByTags(dataWrapper: ServiceByTagHandler): Observable<boolean> {
    return this.httpClient.post<boolean>(this.baseUrl + "/retrieveAllActiveServiceByTags", dataWrapper, httpOptions).pipe(
      catchError(this.handleError)
    );
  }

}

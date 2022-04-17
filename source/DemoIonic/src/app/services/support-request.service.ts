import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Tag } from '../models/tag';
import { SupportRequest } from '../models/support-request';


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class SupportRequestService {

  baseUrl: string = "/api/SupportRequest";

  constructor(private httpClient: HttpClient) { }

  retrieveSupportRequest(username: string, password: string): Observable<SupportRequest[]> {
  return this.httpClient.get<SupportRequest[]>(this.baseUrl + "/RetrieveSupportRequest?username=" + username + "&password=" + password).pipe
      (
        catchError(this.handleError)
      );
  }

  retrieveSupportRequestById(username: string, password: string, supportRequestId: number): Observable<SupportRequest> {
    return this.httpClient.get<SupportRequest>(this.baseUrl + "/RetrieveSupportRequestById?username=" + username + "&password=" + password + "&supportRequestId=" + supportRequestId).pipe(
      catchError(this.handleError)
    );
  }

  updateRequestDetails(username: string, password: string, supportRequestId: number, addRequestDetails: string): Observable<any> {

    console.log("user + pass + number + details = " + username + password + supportRequestId + " " + addRequestDetails);
    return this.httpClient.post<SupportRequest>(this.baseUrl + "/UpdateRequestDetails?username=" + username + "&password=" + password + 
    "&supportRequestId=" + supportRequestId + "&addRequestDetails=" + addRequestDetails,null).pipe(
      catchError(this.handleError)
    );
  }

  createSupportRequest(username: string, password: string, requestDetails: string, bookingId: number): Observable<number> {
    return this.httpClient.put<number>(this.baseUrl + "/CreateSupportrequest?username=" + username + "&password=" + password + "&requestDetails=" 
    + requestDetails + "&bookingId=" + bookingId, null).pipe
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

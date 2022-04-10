import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Tag } from '../models/tag';


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class SupportRequestService {

  constructor(private httpClient: HttpClient) { }

  retrieveAllTags(username: string, password: string): Observable<boolean> {
    return this.httpClient.get<boolean>(this.baseUrl + "/retrieveAllTags?username=" + username + "&password=" + password, null).pipe
      (
        catchError(this.handleError)
      );
  }

  createSupportRequest(username: string, password: string, requestDetails: string, bookingId: number): Observable<Response> {
    return this.httpClient.get<number>(this.baseUrl + "/retrieveAllTags?username=" + username + "&password=" + password + "&requestDetails=" + requestDetails + "&bookingId=" + bookingId, httpOptions).pipe
      (
        catchError(this.handleError)
      );
  }
}

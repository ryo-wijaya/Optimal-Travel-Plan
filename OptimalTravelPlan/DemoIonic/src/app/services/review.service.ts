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
export class ReviewService {

  constructor(private httpClient: HttpClient) { }

  createReview(objHandler: ReviewHandler): Observable<number> {
    return this.httpClient.get<number>(this.baseUrl + "/createReview?username=" + objHandler.customer.username, null).pipe
      (
        catchError(this.handleError)
      );
  }

  updateReview(objHandler: ReviewHandler): Observable<boolean> {
    return this.httpClient.get<boolean>(this.baseUrl + "/updateReview?username=" + objHandler, null).pipe
      (
        catchError(this.handleError)
      );
  }

  deleteReview(objHandler: ReviewHandler): Observable<boolean> {
    return this.httpClient.get<boolean>(this.baseUrl + "/deleteReview?username=" + objHandler, null).pipe
      (
        catchError(this.handleError)
      );
  }
}

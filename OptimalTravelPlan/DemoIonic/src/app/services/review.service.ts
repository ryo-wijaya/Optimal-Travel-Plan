import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ReviewHandler } from '../models/review-handler';
import { Review } from '../models/review';


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class ReviewService {

  baseUrl: string = "/api/Review";

  constructor(private httpClient: HttpClient) { }

  createReview(objHandler: ReviewHandler): Observable<number> {
    console.log("create review with bk id : rating = " + objHandler.bookingId + " : " + objHandler.review.rating);
    return this.httpClient.put<number>(this.baseUrl + "/Create", objHandler, httpOptions).pipe
      (
        catchError(this.handleError)
      );
  }

  updateReview(objHandler: ReviewHandler): Observable<Review> {
    return this.httpClient.post<Review>(this.baseUrl + "/Update", objHandler, httpOptions).pipe
      (
        catchError(this.handleError)
      );
  }

  deleteReview(username: string, password: string, reviewId: number): Observable<boolean> {
    return this.httpClient.delete<boolean>(this.baseUrl + "/Delete/" + reviewId + "?username=" + username + "&password=" + password).pipe
      (
        catchError(this.handleError)
      );
  }

  retrieveReviewsByServiceId(username: string, password: string, serviceId: number): Observable<Review[]> {
    return this.httpClient.get<Review[]>(this.baseUrl + "/retrieveReviewsByServiceId?username=" + username + "&password=" + password
      + "&serviceId=" + serviceId).pipe
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

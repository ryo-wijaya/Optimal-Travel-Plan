/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.DataModel;

import entity.Booking;
import entity.Review;

public class ReviewHandler {
    private Booking booking;
    private Review review;
    private Long newCountryId;
    private String password;

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public Long getNewCountryId() {
        return newCountryId;
    }

    public void setNewCountryId(Long newCountryId) {
        this.newCountryId = newCountryId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
}

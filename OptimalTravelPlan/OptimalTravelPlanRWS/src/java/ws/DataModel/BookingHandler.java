/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.DataModel;

import entity.Booking;
import entity.Customer;

/**
 *
 * @author sucram
 */
public class BookingHandler {
    private Booking booking;
    private Customer customer;
    private Long serviceId;
    private Long travelItineraryId;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getTravelItineraryId() {
        return travelItineraryId;
    }

    public void setTravelItineraryId(Long travelItineraryId) {
        this.travelItineraryId = travelItineraryId;
    }
    
}

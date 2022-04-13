/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.DataModel;

import entity.Customer;
import entity.TravelItinerary;

public class TravelItineraryHandler {
    private Customer customer;
    private TravelItinerary travelItinerary;
    private Long newCountryId;
    private String password;
    private Long travelItineraryId;

    public Long getTravelItineraryId() {
        return travelItineraryId;
    }

    public void setTravelItineraryId(Long travelItineraryId) {
        this.travelItineraryId = travelItineraryId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public TravelItinerary getTravelItinerary() {
        return travelItinerary;
    }

    public void setTravelItinerary(TravelItinerary travelItinerary) {
        this.travelItinerary = travelItinerary;
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

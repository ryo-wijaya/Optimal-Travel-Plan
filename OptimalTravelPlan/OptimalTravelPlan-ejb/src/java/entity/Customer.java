/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Entity
public class Customer extends Account implements Serializable {

    private static final long serialVersionUID = 1L;

    //All customer associations are eargerly fetched to ensure client makes fewer server requests.
    @OneToMany(fetch = FetchType.EAGER)
    private List<Tag> favouriteTags;

    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
    private List<TravelItinerary> travelItineraries;
    
    @OneToMany(fetch = FetchType.EAGER)
    private List<PaymentAccount> paymentAccounts;
    
    private String name;

    private String mobile;

    private String passportNumber;

    private String email;

    private Boolean vaccinationStatus;

    public Customer() {
        this.favouriteTags = new ArrayList<>();
        this.travelItineraries = new ArrayList<>();
        this.paymentAccounts = new ArrayList<>();
    }

    public Customer(String name, String mobile, String passportNumber, String email, Boolean vaccinationStatus, String username, String password) {
        super(username, password);
        this.name = name;
        this.mobile = mobile;
        this.passportNumber = passportNumber;
        this.email = email;
        this.vaccinationStatus = vaccinationStatus;
        this.favouriteTags = new ArrayList<>();
        this.travelItineraries = new ArrayList<>();
    }

    public Long getCustomerId() {
        return getAccountId();
    }

    public List<TravelItinerary> getTravelItineraries() {
        return travelItineraries;
    }

    public List<PaymentAccount> getPaymentAccounts() {
        return paymentAccounts;
    }

    public void setPaymentAccounts(List<PaymentAccount> paymentAccounts) {
        this.paymentAccounts = paymentAccounts;
    }

    public void setTravelItineraries(List<TravelItinerary> travelItineraries) {
        this.travelItineraries = travelItineraries;
    }
    
    public void addTravelItinerary(TravelItinerary travelItinerary){
        this.travelItineraries.add(travelItinerary);
    }
    
    public void removeTravelItinerary(TravelItinerary travelItinerary){
        this.travelItineraries.remove(travelItinerary);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getVaccinationStatus() {
        return vaccinationStatus;
    }

    public void setVaccinationStatus(Boolean vaccinationStatus) {
        this.vaccinationStatus = vaccinationStatus;
    }

    public List<Tag> getFavouriteTags() {
        return favouriteTags;
    }

    public void addFavouriteTag(Tag tag) {
        this.favouriteTags.add(tag);
    }

    public void removeFavouriteTag(Tag tag) {
        this.favouriteTags.remove(tag);
    }

    public void setFavouriteTags(List<Tag> favouriteTags) {
        this.favouriteTags = favouriteTags;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getAccountId() != null ? getAccountId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the getAccountId() fields are not set
        if (!(object instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) object;
        if ((this.getAccountId() == null && other.getAccountId() != null) || (this.getAccountId() != null && !this.getAccountId().equals(other.getAccountId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Customer[ id=" + getAccountId() + " ]";
    }

}

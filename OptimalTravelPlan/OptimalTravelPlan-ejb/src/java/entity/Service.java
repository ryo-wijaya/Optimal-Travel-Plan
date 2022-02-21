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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import util.enumeration.ServiceType;

@Entity
public class Service implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    @ManyToOne
    private Business business;

    @OneToMany
    private List<ServiceRate> rates;

    @OneToOne
    private Country country;

    @OneToMany(mappedBy = "service")
    private List<Booking> bookings;

    @OneToMany(mappedBy = "service")
    private List<Review> reviews;

    private ServiceType serviceType;

    private Boolean requireVaccination;

    private String address;

    public Service() {
        this.rates = new ArrayList<>();
        this.bookings = new ArrayList<>();
    }

    public Service(Business business, Country country, ServiceType serviceType, Boolean requireVaccination, String address) {
        this.business = business;
        this.country = country;
        this.serviceType = serviceType;
        this.requireVaccination = requireVaccination;
        this.address = address;
        this.rates = new ArrayList<>();
        this.bookings = new ArrayList<>();
    }

    public Long getServiceId() {
        return serviceId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (serviceId != null ? serviceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the serviceId fields are not set
        if (!(object instanceof Service)) {
            return false;
        }
        Service other = (Service) object;
        if ((this.serviceId == null && other.serviceId != null) || (this.serviceId != null && !this.serviceId.equals(other.serviceId))) {
            return false;
        }
        return true;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public void addBooking(Booking booking) {
        this.bookings.add(booking);
    }

    public void removeBooking(Booking booking) {
        this.bookings.remove(booking);
    }

    public List<ServiceRate> getRates() {
        return rates;
    }

    public void setRates(List<ServiceRate> rates) {
        this.rates = rates;
    }

    public void addRate(ServiceRate rate) {
        this.rates.add(rate);
    }

    public void removeRate(ServiceRate rate) {
        this.rates.remove(rate);
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public Boolean getRequireVaccination() {
        return requireVaccination;
    }

    public void setRequireVaccination(Boolean requireVaccination) {
        this.requireVaccination = requireVaccination;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "entity.Service[ id=" + serviceId + " ]";
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void addReview(Review review) {
        this.reviews.add(review);
    }

    public void removeReview(Review review) {
        this.reviews.remove(review);
    }

}

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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

    @ManyToOne
    private Country country;

    @OneToMany(mappedBy = "service")
    private List<Booking> bookings;
    
    @ManyToMany(mappedBy = "services")
    private List<Tag> tags;

    private ServiceType serviceType;

    private Boolean requireVaccination;

    private String address;
    
    private Boolean active;
    
    private Integer rating;
    
    private Integer totalNumOfRatings;

    public Service() {
        this.rates = new ArrayList<>();
        this.bookings = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.active = true;
        this.rating = 0;
        this.totalNumOfRatings = 0;
    }

    public Service(Business business, Country country, ServiceType serviceType, Boolean requireVaccination, String address) {
        this();
        this.business = business;
        this.country = country;
        this.serviceType = serviceType;
        this.requireVaccination = requireVaccination;
        this.address = address;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public Integer getTotalNumOfRatings() {
        return totalNumOfRatings;
    }

    public void setTotalNumOfRatings(Integer totalNumOfRatings) {
        this.totalNumOfRatings = totalNumOfRatings;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    /**
     * @return the tags
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

}

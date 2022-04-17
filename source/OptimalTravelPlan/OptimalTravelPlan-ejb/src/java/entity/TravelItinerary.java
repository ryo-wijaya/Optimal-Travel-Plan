/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class TravelItinerary implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long travelItineraryId;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "travelItinerary", fetch = FetchType.LAZY)
    private List<Booking> bookings;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @ManyToOne
    private Country country;

    public TravelItinerary() {
    }

    public void setTravelItineraryId(Long travelItineraryId) {
        this.travelItineraryId = travelItineraryId;
    }
    

    public TravelItinerary(Customer customer, Date startDate, Date endDate, Country country) {
        this.customer = customer;
        this.startDate = startDate;
        this.endDate = endDate;
        this.country = country;
    }

    public void cleanSelf() {
        this.customer = new Customer();
        this.country = new Country();
        this.bookings.clear();
    }

    public void cleanRelationships() {
        try {
            this.customer.cleanSelf();
            this.country.getServices().clear();
            for (Booking bk : this.bookings) {
                Service ser = bk.getService();
                bk.cleanSelf();
                bk.setService(ser);
                ser.cleanSelf();
            }
        } catch (Exception e) {
            System.out.println("Cleaning error!");
        }
    }

    public Long getTravelItineraryId() {
        return travelItineraryId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (travelItineraryId != null ? travelItineraryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the travelItineraryId fields are not set
        if (!(object instanceof TravelItinerary)) {
            return false;
        }
        TravelItinerary other = (TravelItinerary) object;
        if ((this.travelItineraryId == null && other.travelItineraryId != null) || (this.travelItineraryId != null && !this.travelItineraryId.equals(other.travelItineraryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.TravelItinerary[ id=" + travelItineraryId + " ]";
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

}

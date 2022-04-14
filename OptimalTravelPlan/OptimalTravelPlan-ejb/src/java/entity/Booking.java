/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @ManyToOne
    private TravelItinerary travelItinerary;

    @OneToOne
    private PaymentTransaction paymentTransaction;

    @OneToOne(cascade = CascadeType.REMOVE)
    private SupportRequest supportRequest;

    @OneToOne(cascade = CascadeType.REMOVE)
    private Review review;

    @ManyToOne
    private Service service;

    public Booking() {
    }

    public void cleanSelf() {
        this.travelItinerary = new TravelItinerary();
        this.supportRequest = new SupportRequest();
        this.review = new Review();
        this.service = new Service();
    }

    public Booking(Date startDate, Date endDate, TravelItinerary travelItinerary, Service service) {
        this();
        this.startDate = startDate;
        this.endDate = endDate;
        this.travelItinerary = travelItinerary;
        this.service = service;
    }

    public void cleanRelationships() {
        System.out.println("entity.Booking.cleanRelationships()");
        this.travelItinerary.cleanSelf();
        if (this.paymentTransaction != null) {
            this.paymentTransaction.cleanSelf();
        }
        if (this.supportRequest != null) {
            this.supportRequest.cleanSelf();
        }
        if (this.review != null) {
            this.review.cleanSelf();
        }

        this.service.cleanSelf();
        System.out.println("entity.Booking.cleanRelationships() Cleared!");
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getBookingId() {
        return bookingId;
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

    public TravelItinerary getTravelItinerary() {
        return travelItinerary;
    }

    public void setTravelItinerary(TravelItinerary travelItinerary) {
        this.travelItinerary = travelItinerary;
    }

    public PaymentTransaction getPaymentTransaction() {
        return paymentTransaction;
    }

    public void setPaymentTransaction(PaymentTransaction paymentTransaction) {
        this.paymentTransaction = paymentTransaction;
    }

    public SupportRequest getSupportRequest() {
        return supportRequest;
    }

    public void setSupportRequest(SupportRequest supportRequest) {
        this.supportRequest = supportRequest;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bookingId != null ? bookingId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the bookingId fields are not set
        if (!(object instanceof Booking)) {
            return false;
        }
        Booking other = (Booking) object;
        if ((this.bookingId == null && other.bookingId != null) || (this.bookingId != null && !this.bookingId.equals(other.bookingId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Booking[ id=" + bookingId + " ]";
    }

}

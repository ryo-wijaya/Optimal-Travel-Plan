/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class SupportRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supportRequestId;

    @Lob
    @Column(length = 1000000)
    private String requestDetails;

    private Boolean resolved;

    @Temporal(TemporalType.TIMESTAMP)
    private Date requestCreationDate;

    @OneToOne(mappedBy = "supportRequest")
    private Booking booking;

    public SupportRequest() {
        this.resolved = false;
    }

    public SupportRequest(String requestDetails, Date requestCreationDate, Booking booking) {
        this();
        this.requestDetails = requestDetails;
        this.requestCreationDate = requestCreationDate;
        this.booking = booking;
    }

    public Long getSupportRequestId() {
        return supportRequestId;
    }

    public String getRequestDetails() {
        return requestDetails;
    }

    public Boolean getResolved() {
        return resolved;
    }

    public void setResolved(Boolean resolved) {
        this.resolved = resolved;
    }

    public void setRequestDetails(String requestDetails) {
        this.requestDetails = requestDetails;
    }

    public Date getRequestCreationDate() {
        return requestCreationDate;
    }

    public void setRequestCreationDate(Date requestCreationDate) {
        this.requestCreationDate = requestCreationDate;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (supportRequestId != null ? supportRequestId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the supportRequestId fields are not set
        if (!(object instanceof SupportRequest)) {
            return false;
        }
        SupportRequest other = (SupportRequest) object;
        if ((this.supportRequestId == null && other.supportRequestId != null) || (this.supportRequestId != null && !this.supportRequestId.equals(other.supportRequestId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.SupportRequest[ id=" + supportRequestId + " ]";
    }

}

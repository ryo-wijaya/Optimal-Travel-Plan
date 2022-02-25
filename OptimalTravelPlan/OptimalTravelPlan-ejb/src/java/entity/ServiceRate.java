/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import util.enumeration.ChargeType;
import util.enumeration.RateType;

@Entity
public class ServiceRate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceRateId;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    
    private BigDecimal price;
    
    private RateType rateType;
    
    private Boolean requireVaccination;
    
    private Boolean enabled;
    
    private ChargeType chargeType;

    public ServiceRate() {
        enabled = true;
    }

    public ServiceRate(Date startDate, Date endDate, BigDecimal price, RateType rateType, Boolean requireVaccination, Boolean enabled, ChargeType chargeType) {
        this();
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.rateType = rateType;
        this.requireVaccination = requireVaccination;
        this.enabled = enabled;
        this.chargeType = chargeType;
    }

    public Long getServiceRateId() {
        return serviceRateId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (serviceRateId != null ? serviceRateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the serviceRateId fields are not set
        if (!(object instanceof ServiceRate)) {
            return false;
        }
        ServiceRate other = (ServiceRate) object;
        if ((this.serviceRateId == null && other.serviceRateId != null) || (this.serviceRateId != null && !this.serviceRateId.equals(other.serviceRateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ServiceRate[ id=" + serviceRateId + " ]";
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public RateType getRateType() {
        return rateType;
    }

    public void setRateType(RateType rateType) {
        this.rateType = rateType;
    }
    
    public int compareTo(ServiceRate other) {
        if(other.getRateType() == RateType.PROMOTION) {
            if(this.getRateType() == RateType.PROMOTION) {
                return this.getPrice().compareTo(other.getPrice());
            } else {
                return -1;
            }
        }else if(other.getRateType() == RateType.NORMAL){
            if(this.getRateType()== RateType.NORMAL) {
                return this.getPrice().compareTo(other.getPrice());
            }else {
                return 1;
            }
        }
        if(this.getRateType() == RateType.NORMAL){
            return -1;
        }
        if(this.getRateType() == RateType.PROMOTION){
            return 1;
        }
        return this.getPrice().compareTo(other.getPrice());
    }

    public Boolean getRequireVaccination() {
        return requireVaccination;
    }

    public void setRequireVaccination(Boolean requireVaccination) {
        this.requireVaccination = requireVaccination;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the chargeType
     */
    public ChargeType getChargeType() {
        return chargeType;
    }

    /**
     * @param chargeType the chargeType to set
     */
    public void setChargeType(ChargeType chargeType) {
        this.chargeType = chargeType;
    }
}

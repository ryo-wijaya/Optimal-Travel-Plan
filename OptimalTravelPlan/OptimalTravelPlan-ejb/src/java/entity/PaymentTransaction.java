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
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class PaymentTransaction implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentTransactionId;

    @ManyToOne
    private PaymentAccount paymentAccount;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfPayment;
    
    private String transactionNumber;
    
    private BigDecimal prevailingRateAtPaymentDate;

    public PaymentTransaction() {
    }

    public PaymentTransaction(PaymentAccount paymentAccount, Date dateOfPayment, String transactionNumber, BigDecimal prevailingRateAtPaymentDate) {
        this.paymentAccount = paymentAccount;
        this.dateOfPayment = dateOfPayment;
        this.transactionNumber = transactionNumber;
        this.prevailingRateAtPaymentDate = prevailingRateAtPaymentDate;
    }
    
    public Long getPaymentTransactionId() {
        return paymentTransactionId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (paymentTransactionId != null ? paymentTransactionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the paymentTransactionId fields are not set
        if (!(object instanceof PaymentTransaction)) {
            return false;
        }
        PaymentTransaction other = (PaymentTransaction) object;
        if ((this.paymentTransactionId == null && other.paymentTransactionId != null) || (this.paymentTransactionId != null && !this.paymentTransactionId.equals(other.paymentTransactionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Payment[ id=" + paymentTransactionId + " ]";
    }

    public PaymentAccount getPaymentAccount() {
        return paymentAccount;
    }

    public void setPaymentAccount(PaymentAccount paymentAccount) {
        this.paymentAccount = paymentAccount;
    }

    public Date getDateOfPayment() {
        return dateOfPayment;
    }

    public void setDateOfPayment(Date dateOfPayment) {
        this.dateOfPayment = dateOfPayment;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public BigDecimal getPrevailingRateAtPaymentDate() {
        return prevailingRateAtPaymentDate;
    }

    public void setPrevailingRateAtPaymentDate(BigDecimal prevailingRateAtPaymentDate) {
        this.prevailingRateAtPaymentDate = prevailingRateAtPaymentDate;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.DataModel;

import entity.Customer;
import entity.PaymentAccount;

/**
 *
 * @author sucram
 */
public class PaymentAccountHandler {
    
    private PaymentAccount paymentAccount;
    private Customer customer;
    private String password;
    private Integer date;

    public PaymentAccount getPaymentAccount() {
        return paymentAccount;
    }

    public void setPaymentAccount(PaymentAccount paymentAccount) {
        this.paymentAccount = paymentAccount;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }
}

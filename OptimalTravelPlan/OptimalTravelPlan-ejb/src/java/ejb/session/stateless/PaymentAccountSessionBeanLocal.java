/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.PaymentAccount;
import java.util.List;
import javax.ejb.Local;
import util.exception.AccountNotFoundException;
import util.exception.DeletePaymentAccountException;
import util.exception.PaymentAccountNotFoundException;

/**
 *
 * @author Jorda
 */
@Local
public interface PaymentAccountSessionBeanLocal {

    public PaymentAccount createNewPaymentAccount(Long CustomerId, PaymentAccount newPaymentAccount) throws AccountNotFoundException;

    public PaymentAccount retrievePaymentAccountByPaymentAccountId(Long paymentAccountId) throws PaymentAccountNotFoundException;

    public void deletePaymentAccount(Long paymentAccountId) throws DeletePaymentAccountException;

    public void updatePaymentAccount(PaymentAccount paymentAccount) throws PaymentAccountNotFoundException;

    public List<PaymentAccount> retrieveAllCustomerPaymentAccounts(Long customerId) throws AccountNotFoundException;
    
}

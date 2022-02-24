/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.PaymentAccount;
import javax.ejb.Local;
import util.exception.DeletePaymentAccountException;
import util.exception.PaymentAccountNotFoundException;

/**
 *
 * @author Jorda
 */
@Local
public interface PaymentAccountSessionBeanLocal {

    public PaymentAccount createNewPaymentAccount(PaymentAccount newPaymentAccount);

    public PaymentAccount retrievePaymentAccountByPaymentAccountId(Long paymentAccountId) throws PaymentAccountNotFoundException;

    public void deletePaymentAccount(Long paymentAccountId) throws DeletePaymentAccountException;

    public void updatePaymentAccount(PaymentAccount paymentAccount) throws PaymentAccountNotFoundException;
    
}

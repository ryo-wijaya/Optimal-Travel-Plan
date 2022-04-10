/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.PaymentTransaction;
import java.util.List;
import javax.ejb.Local;
import util.exception.AccountNotFoundException;
import util.exception.BookingNotFoundException;
import util.exception.ConstraintViolationException;
import util.exception.PaymentAccountNotFoundException;
import util.exception.PaymentTransactionNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author sucram
 */
@Local
public interface TransactionSessionBeanLocal {

    public List<PaymentTransaction> retrieveAllPaymentTransaction();

    public PaymentTransaction retrievePaymentTransactionByTransactionId(Long transactionId) throws PaymentTransactionNotFoundException;

    public PaymentTransaction createNewPaymentTransaction(PaymentTransaction paymentTransaction, Long bookingId) throws ConstraintViolationException, UnknownPersistenceException, BookingNotFoundException;
    
    public PaymentTransaction makePayment(Long bookingId, Long paymentAccountId) throws BookingNotFoundException, PaymentAccountNotFoundException, ConstraintViolationException, UnknownPersistenceException;

    public List<PaymentTransaction> retrievePaymentTransactionsByCustomerId(Long customerId) throws AccountNotFoundException;
}

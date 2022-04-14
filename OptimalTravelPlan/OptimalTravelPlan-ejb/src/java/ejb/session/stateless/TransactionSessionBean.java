/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Booking;
import entity.Customer;
import entity.PaymentAccount;
import entity.PaymentTransaction;
import entity.TravelItinerary;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.AccountNotFoundException;
import util.exception.BookingNotFoundException;
import util.exception.ConstraintViolationException;
import util.exception.PaymentAccountNotFoundException;
import util.exception.PaymentTransactionNotFoundException;
import util.exception.UnknownPersistenceException;

@Stateless
public class TransactionSessionBean implements TransactionSessionBeanLocal {

    @EJB(name = "CustomerSessionBeanLocal")
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    @EJB(name = "PaymentAccountSessionBeanLocal")
    private PaymentAccountSessionBeanLocal paymentAccountSessionBeanLocal;

    @EJB
    private BookingSessionBeanLocal bookingSessionBeanLocal;

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;

    @Override
    public PaymentTransaction createNewPaymentTransaction(PaymentTransaction paymentTransaction, Long bookingId) throws ConstraintViolationException, UnknownPersistenceException, BookingNotFoundException {
        try {

            Booking bookingToAssociate = bookingSessionBeanLocal.retrieveBookingById(bookingId);
            Double d = Math.random() * 9999999999999999l;
            d = Math.floor(d);
            if (d < 10000000000l) {
                d += 10000000000l;
            }
            d %= 10000000000l;
            String code = "";
            int k = d.intValue();
            while (k > 0) {
                code = "" + (k % 10) + code;
                k /= 10;
            }
            while (code.length() < 10) {
                code = "0" + code;
            }

            paymentTransaction.setTransactionNumber(code);

            paymentTransaction.setPrevailingRateAtPaymentDate(bookingSessionBeanLocal.getPricingOfBooking(bookingId, bookingToAssociate.getStartDate(), bookingToAssociate.getEndDate()));

            em.persist(paymentTransaction);

            bookingToAssociate.setPaymentTransaction(paymentTransaction);

            em.flush();
            return paymentTransaction;
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new ConstraintViolationException();
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }

    @Override
    public List<PaymentTransaction> retrievePaymentTransactionsByCustomerId(Long customerId) throws AccountNotFoundException {
        Customer customer = customerSessionBeanLocal.retrieveCustomerById(customerId);
        List<PaymentTransaction> list = new ArrayList<>();
        for (TravelItinerary travelItinerary : customer.getTravelItineraries()) {
            for (Booking bk : travelItinerary.getBookings()) {
                list.add(bk.getPaymentTransaction());
            }
        }
        return list;
    }

    @Override
    public PaymentTransaction makePayment(Long bookingId, Long PaymentAccountId) throws BookingNotFoundException, PaymentAccountNotFoundException, ConstraintViolationException, UnknownPersistenceException {
        PaymentAccount account = paymentAccountSessionBeanLocal.retrievePaymentAccountByPaymentAccountId(PaymentAccountId);
        Booking booking = bookingSessionBeanLocal.retrieveBookingById(bookingId);
        PaymentTransaction newPaymentTransaction = new PaymentTransaction(account, new Date(), "asd",
                bookingSessionBeanLocal.getPricingOfBooking(bookingId, booking.getStartDate(), booking.getEndDate()));
        newPaymentTransaction.setPaymentAccount(account);
        return createNewPaymentTransaction(newPaymentTransaction, bookingId);
    }

    @Override
    public List<PaymentTransaction> retrieveAllPaymentTransaction() {
        Query query = em.createQuery("SELECT p FROM PaymentTransaction p");
        List<PaymentTransaction> paymentTransactions = query.getResultList();
        for (PaymentTransaction p : paymentTransactions) {
            p.getPaymentAccount();
        }
        return paymentTransactions;
    }

    @Override
    public PaymentTransaction retrievePaymentTransactionByTransactionId(Long transactionId) throws PaymentTransactionNotFoundException {
        PaymentTransaction paymentTransaction = em.find(PaymentTransaction.class, transactionId);
        if (paymentTransaction != null) {
            return paymentTransaction;
        } else {
            throw new PaymentTransactionNotFoundException();
        }
    }

}

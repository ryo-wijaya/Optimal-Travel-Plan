/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Booking;
import entity.PaymentTransaction;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.BookingNotFoundException;
import util.exception.ConstraintViolationException;
import util.exception.PaymentTransactionNotFoundException;
import util.exception.UnknownPersistenceException;

@Stateless
public class TransactionSessionBean implements TransactionSessionBeanLocal {

    @EJB
    private BookingSessionBeanLocal bookingSessionBeanLocal;

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;
    
    
    
    @Override
    public PaymentTransaction createNewPaymentTransaction(PaymentTransaction paymentTransaction, Long bookingId) throws ConstraintViolationException, UnknownPersistenceException, BookingNotFoundException{
        try {
            em.persist(paymentTransaction);
            
            Booking bookingToAssociate = bookingSessionBeanLocal.retrieveBookingById(bookingId);
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
    public List<PaymentTransaction> retrieveAllPaymentTransaction(){
        Query query = em.createQuery("SELECT p FROM PaymentTransaction p");
        List<PaymentTransaction> paymentTransactions = query.getResultList();
        for(PaymentTransaction p : paymentTransactions){
            p.getPaymentAccount();
        }
        return paymentTransactions;
    }

    @Override
    public PaymentTransaction retrievePaymentTransactionByTransactionId(Long transactionId) throws PaymentTransactionNotFoundException {
        PaymentTransaction paymentTransaction = em.find(PaymentTransaction.class, transactionId);
        if(paymentTransaction != null) {
            return paymentTransaction;
        } else {
            throw new PaymentTransactionNotFoundException();
        }
    }
    
}

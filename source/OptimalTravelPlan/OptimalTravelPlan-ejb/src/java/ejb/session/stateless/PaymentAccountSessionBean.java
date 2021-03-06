/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import entity.PaymentAccount;
import entity.PaymentTransaction;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.AccountNotFoundException;
import util.exception.DeletePaymentAccountException;
import util.exception.PaymentAccountNotFoundException;

/**
 *
 * @author Jorda
 */
@Stateless
public class PaymentAccountSessionBean implements PaymentAccountSessionBeanLocal {

    @EJB
    private TransactionSessionBeanLocal transactionSessionBean;
    
    

    @EJB
    private CustomerSessionBeanLocal customerSessionBean;

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public PaymentAccount createNewPaymentAccount(Long CustomerId, PaymentAccount newPaymentAccount) throws AccountNotFoundException {
        em.persist(newPaymentAccount);
        em.flush();
        Customer customer = customerSessionBean.retrieveCustomerById(CustomerId);
        customer.getPaymentAccounts().add(newPaymentAccount);
        
        return newPaymentAccount;
    }
    
    @Override
    public List<PaymentAccount> retrieveAllCustomerPaymentAccounts(Long customerId) throws AccountNotFoundException{
        Customer customer = customerSessionBean.retrieveCustomerById(customerId);
        List<PaymentAccount> accs = customer.getPaymentAccounts();
        
        for (int i = 0; i < accs.size(); i++) {
            if (!accs.get(i).getEnabled()) {
                accs.remove(i);
            }
        }
        return accs;
    }
    
    @Override
    public PaymentAccount retrievePaymentAccountByPaymentAccountId(Long paymentAccountId) throws PaymentAccountNotFoundException{
        PaymentAccount paymentAccount = em.find(PaymentAccount.class, paymentAccountId);
        if(paymentAccount != null){
            return paymentAccount;
        } else {
            throw new PaymentAccountNotFoundException("Payment Account ID " + paymentAccountId + " does not exist!");
        }
    }
    
    @Override
    public void deletePaymentAccount(Long paymentAccountId) throws DeletePaymentAccountException{
        PaymentAccount paymentAccountToDelete = em.find(PaymentAccount.class, paymentAccountId);
        List<Customer> customers = customerSessionBean.retrieveAllCustomers();
        for(Customer c : customers){
            if (c.getPaymentAccounts().contains(paymentAccountToDelete)) {
                throw new DeletePaymentAccountException("Payment Account ID " + paymentAccountId + " is associated with existing customers and cannot be deleted!");
            }
        }
        List<PaymentTransaction> paymentTransactions = transactionSessionBean.retrieveAllPaymentTransaction();
        for(PaymentTransaction p : paymentTransactions){
            p.setPaymentAccount(null);
        }
        em.remove(paymentAccountToDelete);
    }
    
    @Override
    public void toggleAccountStatus(Long paymentAccountId) throws DeletePaymentAccountException {
        PaymentAccount paymentAccountToDisable = em.find(PaymentAccount.class, paymentAccountId);;

        if (paymentAccountToDisable != null) {
            Boolean newStatus = paymentAccountToDisable.getEnabled() ? false : true; // not redundant
            paymentAccountToDisable.setEnabled(newStatus);
        } else {
            throw new DeletePaymentAccountException("ID not provided for account status to be updated");
        }
    }
    
    
    
    @Override
    public void updatePaymentAccount(PaymentAccount paymentAccount) throws PaymentAccountNotFoundException{
        if (paymentAccount != null && paymentAccount.getPaymenetAccountId()!= null) {
            PaymentAccount paymentAccountToUpdate = this.retrievePaymentAccountByPaymentAccountId(paymentAccount.getPaymenetAccountId());

            paymentAccountToUpdate.setAccountNumber(paymentAccount.getAccountNumber());
            paymentAccountToUpdate.setCardExpirationDate(paymentAccount.getCardExpirationDate());
            paymentAccountToUpdate.setCcv(paymentAccount.getCcv());
            paymentAccountToUpdate.setPaymentType(paymentAccount.getPaymentType());
            paymentAccountToUpdate.setEnabled(paymentAccount.getEnabled());
            
        } else {
            throw new PaymentAccountNotFoundException("Payment Account ID not provided for payment account to be updated");
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.PaymentTransaction;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class TransactionSessionBean implements TransactionSessionBeanLocal {

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;
    
    @Override
    public List<PaymentTransaction> retrieveAllPaymentTransaction(){
        Query query = em.createQuery("SELECT p FROM PaymentTransaction p");
        List<PaymentTransaction> paymentTransactions = query.getResultList();
        for(PaymentTransaction p : paymentTransactions){
            p.getPaymentAccount();
        }
        return paymentTransactions;
    }

}

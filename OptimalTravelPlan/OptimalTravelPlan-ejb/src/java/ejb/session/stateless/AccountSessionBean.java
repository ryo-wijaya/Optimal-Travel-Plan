/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Account;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import util.exception.AccountNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UsernameAlreadyExistException;

/**
 *
 * @author sucram
 */
@Stateless
public class AccountSessionBean implements AccountSessionBeanLocal {

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewAccount(Account newAccount) throws UsernameAlreadyExistException, UnknownPersistenceException {
        try {
            em.persist(newAccount);
            em.flush();

            return newAccount.getAccountId();
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new UsernameAlreadyExistException();
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }
    
    private Account retrieveAccountById(Long accountId) throws AccountNotFoundException {
        Account account = em.find(Account.class, accountId);
        if (account != null) {
            return account;
        } else {
            throw new AccountNotFoundException();
        }
    }
    
    @Override
    public void toggleAccountStatus(Long accountId) throws AccountNotFoundException {
        Account account = retrieveAccountById(accountId);
        
        if (account != null && account.getAccountId() != null) {    
            Boolean newStatus = account.getEnabled() ? false : true;
            account.setEnabled(newStatus);
        } else {
            throw new AccountNotFoundException("ID not provided for account status to be updated");
        }
    }
}

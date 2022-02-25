/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Account;
import entity.Business;
import entity.Customer;
import entity.Staff;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.AccountDisabledException;
import util.exception.AccountNotFoundException;
import util.exception.ChangePasswordException;
import util.exception.InvalidLoginCredentialException;
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
                    throw new UsernameAlreadyExistException("Username already exists!");
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
            Boolean newStatus = account.getEnabled() ? false : true; // not redundant
            account.setEnabled(newStatus);
        } else {
            throw new AccountNotFoundException("ID not provided for account status to be updated");
        }
    }

    @Override
    public Account login(String username, String password) throws InvalidLoginCredentialException, AccountDisabledException {

        Query query = em.createQuery("SELECT a FROM Account a WHERE a.username = :inUsername");
        query.setParameter("inUsername", username);
        Account acc;
        try {
            acc = (Account) query.getSingleResult();
        } catch (Exception e){
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
        if (acc.testPassword(password)) {
            if (!acc.getEnabled()) {
                throw new AccountDisabledException("Account has been disabled! Please contact administrator!");
            }
            if (acc instanceof Customer || acc instanceof Staff) {
                //Customer associations are always eargerly fetched to to ensure client make less server requests.
                //Staffs do not have associations
                return acc;
            } else if (acc instanceof Business) {
                Business business = (Business) acc;
                business.getServices();
                return business;
            }
        }
        throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
    }
    
    @Override
    public void changePassword(String oldPassword, String newPassword, Long accountId) throws AccountNotFoundException, ChangePasswordException {
        Account account = this.retrieveAccountById(accountId);
        
        if (account.testPassword(oldPassword) && (newPassword.length() > 6 && newPassword.length() <= 16)) {
            account.setPassword(account.hashPassword(newPassword));
        } else {
            throw new ChangePasswordException("Password does not match!");
        }
    }
}

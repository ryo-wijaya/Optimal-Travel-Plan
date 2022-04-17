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
import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.AccountDisabledException;
import util.exception.AccountNotFoundException;
import util.exception.ChangePasswordException;
import util.exception.InvalidLoginCredentialException;
import util.exception.PasswordNotAcceptedException;
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

    @Override
    public Account retrieveAccountById(Long accountId) throws AccountNotFoundException {
        System.out.println("ejb.session.stateless.AccountSessionBean.retrieveAccountById() account id = " + accountId);
        Account account = em.find(Account.class, accountId);
        System.out.println("ejb.session.stateless.AccountSessionBean.retrieveAccountById() account = " + account);
        if (account != null) {
            return account;
        } else {
            throw new AccountNotFoundException("Account id " + accountId + " is not found!");
        }
    }

    @Override
    public void toggleAccountStatus(Long accountId) throws AccountNotFoundException {
        
        System.out.println("ejb.session.stateless.AccountSessionBean.toggleAccountStatus() account id = " + accountId);
        Account account = retrieveAccountById(accountId);
        System.out.println("ejb.session.stateless.AccountSessionBean.toggleAccountStatus() account id = " + account);
        
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

        } catch (Exception e) {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
        if (acc.testPassword(password)) {
            if (!acc.getEnabled()) {
                throw new AccountDisabledException("Account has been disabled! Please contact administrator!");
            }
            if (acc instanceof Customer || acc instanceof Staff) {
                return acc;
            } else if (acc instanceof Business) {
                Business business = (Business) acc;
                business.getServices().size();
                return business;
            }
        }
        throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
    }

    @Override
    public void changePassword(String oldPassword, String newPassword, Long accountId) throws AccountNotFoundException, ChangePasswordException, PasswordNotAcceptedException {
        Account account = this.retrieveAccountById(accountId);

        if (account.testPassword(oldPassword)) {
            account.setPassword(newPassword);
        } else {
            throw new ChangePasswordException("Password does not match!");
        }
    }

    @Override
    public String forgetPasswordChange(Long accountId) {
        try {
            Account account = this.retrieveAccountById(accountId);

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

            account.setPassword(code);
            return code;

        } catch (AccountNotFoundException | PasswordNotAcceptedException ex) {
            // Do nothing as account is guaranteed to be found and password is guaranteed to be accepted
            return null;
        }
    }
}

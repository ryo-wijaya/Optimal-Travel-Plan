/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Business;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.AccountNotFoundException;
import util.exception.InvalidLoginCredentialsException;
import util.exception.UpdateBusinessException;

/**
 *
 * @author ryo20
 */
@Stateless
public class BusinessSessionBean implements BusinessSessionBeanLocal {

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;

    @Override
    public List<Business> retrieveAllBusinesses() {
        Query query = em.createQuery("SELECT b FROM Business b");

        List<Business> businesses = query.getResultList();
        for (Business b : businesses) { //lazy loading
            b.getServices().size();
        }
        return businesses;
    }

    @Override
    public Business retrieveBusinessById(Long businessId) throws AccountNotFoundException {
        Business business = em.find(Business.class, businessId);
        if (business != null) {//lazy loading
            business.getServices().size();
            return business;
        } else {
            throw new AccountNotFoundException();
        }
    }

    @Override
    public Business retrieveBusinessByUsername(String username) throws AccountNotFoundException {
        Query query = em.createQuery("SELECT b FROM Business b WHERE b.username = :inUsername");
        query.setParameter("inUsername", username);
        try {
            Business business = (Business) query.getSingleResult();
            business.getServices().size(); //lazy loading
            return business;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new AccountNotFoundException("Username has no match!");
        }
    }

    @Override
    public Business doBusinessLogin(String username, String password) throws InvalidLoginCredentialsException {
        try {
            Business business = retrieveBusinessByUsername(username);
            String passwordHash = new String(business.doMD5Hashing(password + business.getSalt()));

            if (passwordHash.equals(business.getPassword())) {
                business.getServices().size(); //lazy loading
                return business;
            } else {
                throw new InvalidLoginCredentialsException();
            }
        } catch (AccountNotFoundException ex) {
            throw new InvalidLoginCredentialsException();
        }
    }

    @Override
    public void updateBusiness(Business business) throws AccountNotFoundException, UpdateBusinessException {
        if (business != null && business.getBusinessId() != null) {
            Business businessToUpdate = this.retrieveBusinessById(business.getBusinessId());

            if (businessToUpdate.getUsername().equals(business.getUsername())) {
                businessToUpdate.setCompanyName(business.getCompanyName());
                businessToUpdate.setCompanyNumber(business.getCompanyNumber());
                businessToUpdate.setCompanyWebsite(business.getCompanyWebsite());
                businessToUpdate.setHeadquarterAddress(business.getHeadquarterAddress());
            } else {
                throw new UpdateBusinessException("Username of business record to be updated does not match the existing record");
            }
        } else {
            throw new AccountNotFoundException("Business ID not provided for business to be updated");
        }
    }
}

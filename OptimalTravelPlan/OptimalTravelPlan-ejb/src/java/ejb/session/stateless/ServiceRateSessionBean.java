/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Service;
import entity.ServiceRate;
import entity.Tag;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.ConstraintViolationException;
import util.exception.CountryNotFoundException;
import util.exception.CreateNewServiceException;
import util.exception.CreateNewServiceRateException;
import util.exception.DeleteServiceRateException;
import util.exception.ServiceNotFoundException;
import util.exception.ServiceRateNotFoundException;
import util.exception.TagNotFoundException;
import util.exception.UnknownPersistenceException;

@Stateless
public class ServiceRateSessionBean implements ServiceRateSessionBeanLocal {

    @EJB
    private ServiceSessionBeanLocal serviceSessionBeanLocal;

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;

    public Long createNewServiceRate(ServiceRate newServiceRate, Long serviceId) throws UnknownPersistenceException, ConstraintViolationException, CreateNewServiceRateException {
        try {
            Service service = serviceSessionBeanLocal.retrieveServiceById(serviceId);
            service.getRates().add(newServiceRate);
            em.persist(newServiceRate);
            em.flush();
            return newServiceRate.getServiceRateId();

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
        } catch (ServiceNotFoundException ex) {
            throw new CreateNewServiceRateException("Issue with provided ServiceId!");
        }
    }

    @Override
    public ServiceRate retrieveServiceRateById(Long serviceRateId) throws ServiceRateNotFoundException {
        ServiceRate serviceRate = em.find(ServiceRate.class, serviceRateId);
        if (serviceRate != null) {
            return serviceRate;
        } else {
            throw new ServiceRateNotFoundException();
        }
    }

    // Since Service to ServiceRate is 0..*
    // might wanna revamp this actually
    public void deleteServiceRate(Long serviceRateId) throws ServiceRateNotFoundException, DeleteServiceRateException {
        ServiceRate serviceRate = this.retrieveServiceRateById(serviceRateId);
        Query query = em.createQuery("SELECT s FROM Service s WHERE :rateToDelete MEMBER OF s.rates");
        query.setParameter("rateToDelete", serviceRate);
        try {
            Service service = (Service) query.getSingleResult();
            service.getRates().remove(serviceRate);
            em.remove(serviceRate);
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new DeleteServiceRateException("No serviceRate ID found!");
        }
    }
}


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Service;
import entity.ServiceRate;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import util.exception.ConstraintViolationException;
import util.exception.CreateNewServiceRateException;
import util.exception.ServiceNotFoundException;
import util.exception.ServiceRateNotFoundException;
import util.exception.UnknownPersistenceException;

@Stateless
public class ServiceRateSessionBean implements ServiceRateSessionBeanLocal {

    @EJB
    private ServiceSessionBeanLocal serviceSessionBeanLocal;

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewServiceRate(ServiceRate newServiceRate, Long serviceId) throws UnknownPersistenceException, ConstraintViolationException, CreateNewServiceRateException {
        try {
            Service service = serviceSessionBeanLocal.retrieveServiceById(serviceId);
            em.persist(newServiceRate);
            service.getRates().add(newServiceRate);
            em.flush();
            return newServiceRate.getServiceRateId();

        } catch (PersistenceException ex) {
            throw new UnknownPersistenceException(ex.getMessage());
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

    // deleted deleteServiceRate()
    @Override
    public void toggleServiceRateActivation(Long serviceRateId) throws ServiceRateNotFoundException {
        ServiceRate serviceRate = this.retrieveServiceRateById(serviceRateId);
        if (serviceRate != null && serviceRate.getServiceRateId() != null) {
            Boolean newStatus = serviceRate.getEnabled() ? false : true;
            serviceRate.setEnabled(newStatus);
        } else {
            throw new ServiceRateNotFoundException("ID not provided for serviceRate status to be updated");
        }
    }

    @Override
    public List<ServiceRate> retrieveServiceRateByServiceId(Long serviceID) throws ServiceNotFoundException {
        return this.serviceSessionBeanLocal.retrieveServiceById(serviceID).getRates();
    }

    @Override
    public ServiceRate updateServiceRate(ServiceRate serviceRate) throws ServiceRateNotFoundException, ConstraintViolationException {
        ServiceRate rate = retrieveServiceRateById(serviceRate.getServiceRateId());
        Date today = new Date();
        //ensure that can only extend 
        if (rate.getStartDate().compareTo(serviceRate.getStartDate()) >= 0
                && rate.getEndDate().compareTo(serviceRate.getStartDate()) <= 0) {
            if (rate.getPrice() != serviceRate.getPrice()){
                throw new ConstraintViolationException("Unable to change rate price! Please create a new rate and disable old one!");
            }
            rate.setChargeType(serviceRate.getChargeType());
            rate.setEndDate(serviceRate.getEndDate());
            rate.setStartDate(serviceRate.getStartDate());
            rate.setChargeType(serviceRate.getChargeType());
            em.flush();
            return rate;
        }
        throw new ConstraintViolationException("Only allowed to extend rate or make it start earlier!");
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Service;
import entity.ServiceRate;
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
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Country;
import entity.Service;
import entity.ServiceRate;
import entity.Tag;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.AccountNotFoundException;
import util.exception.ConstraintViolationException;
import util.exception.CountryNotFoundException;
import util.exception.CreateNewServiceException;
import util.exception.ServiceNotFoundException;
import util.exception.TagNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateServiceException;

@Stateless
public class ServiceSessionBean implements ServiceSessionBeanLocal {

    @EJB
    private CountrySessionBeanLocal countrySessionBeanLocal;

    @EJB
    private ServiceRateSessionBeanLocal serviceRateSessionBeanLocal;

    @EJB
    private TagSessionBeanLocal tagSessionBeanLocal;

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewService(Service newService, Long businessId, List<Long> tagIds, Long countryId) throws UnknownPersistenceException, ConstraintViolationException,
            CreateNewServiceException {
        try {
            List<Tag> tagsToAssociate = new ArrayList<>();

            for (Long tagId : tagIds) {
                tagsToAssociate.add(tagSessionBeanLocal.retrieveTagByTagId(tagId));
            }

            //A Service must have at least 1 tag
            if (tagsToAssociate.isEmpty()) {
                throw new CreateNewServiceException();
            }

            Country countryToAssociate = countrySessionBeanLocal.retrieveCountryByCountryId(countryId);
            newService.setCountry(countryToAssociate);
            em.persist(newService);
            
            for (Tag tag : tagsToAssociate) {
                tag.getServices().add(newService);
            }
            
            countryToAssociate.getServices().add(newService);
            newService.getTags().addAll(tagsToAssociate);
            em.flush();
            return newService.getServiceId();

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
        } catch (CountryNotFoundException | TagNotFoundException ex) {
            throw new CreateNewServiceException("Issue with provided businessId, TagIds, RateIds, or CountryIds!");
        }
    }

    @Override
    public Service retrieveServiceById(Long serviceId) throws ServiceNotFoundException {
        Service service = em.find(Service.class, serviceId);
        if (service != null) {
            service.getBookings().size(); //lazy loading
            service.getBusiness();
            service.getRates().size();
            service.getCountry();
            service.getTags().size();
            return service;
        } else {
            throw new ServiceNotFoundException();
        }
    }

    @Override
    public List<Service> retrieveAllServices() {
        Query query = em.createQuery("SELECT s FROM Service s");
        List<Service> services = query.getResultList();
        for (Service service : services) {
            service.getBookings().size(); //lazy loading
            service.getBusiness();
            service.getRates().size();
            service.getCountry();
            service.getTags().size();
        }
        return services;
    }
    
    @Override
    public List<Service> retrieveAllActiveServices() {
        Query query = em.createQuery("SELECT s FROM Service s WHERE s.active = true");
        List<Service> services = query.getResultList();
        for (Service service : services) {
            service.getBookings().size(); //lazy loading
            service.getBusiness();
            service.getRates().size();
            service.getCountry();
            service.getTags().size();
        }
        return services;
    }

    @Override
    public void updateService(Service newService) throws ServiceNotFoundException, UpdateServiceException, AccountNotFoundException {
        if (newService != null && newService.getServiceId() != null) {
            Service serviceToUpdate = this.retrieveServiceById(newService.getServiceId());

            serviceToUpdate.setCountry(newService.getCountry());
            serviceToUpdate.setRates(newService.getRates());
            serviceToUpdate.setServiceType(newService.getServiceType());
            serviceToUpdate.setAddress(newService.getAddress());
            serviceToUpdate.setRequireVaccination(newService.getRequireVaccination());
            serviceToUpdate.setTags(newService.getTags());

        } else {
            throw new AccountNotFoundException("Service ID not provided for service to be updated");
        }
    }
    
    // Implemented deactivation of service instead of deletion to preserve Booking records
    @Override
    public void toggleServiceActivation(Long serviceId) throws ServiceNotFoundException {
        Service service = this.retrieveServiceById(serviceId);
        if (service != null && service.getServiceId()!= null) {
            Boolean newStatus = service.getActive() ? false : true;
            service.setActive(newStatus);
        } else {
            throw new ServiceNotFoundException("ID not provided for account status to be updated");
        }
    }
}

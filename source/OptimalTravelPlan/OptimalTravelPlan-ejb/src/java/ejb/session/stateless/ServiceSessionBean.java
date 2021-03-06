/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Business;
import entity.Country;
import entity.Service;
import entity.ServiceRate;
import entity.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.enumeration.ServiceType;
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
    private BusinessSessionBeanLocal businessSessionBeanLocal;

    @EJB
    private CountrySessionBeanLocal countrySessionBeanLocal;

    @EJB
    private ServiceRateSessionBeanLocal serviceRateSessionBeanLocal;

    @EJB
    private TagSessionBeanLocal tagSessionBeanLocal;

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewService(Service newService, Long businessId, List<Long> tagIds, Long countryId) throws UnknownPersistenceException, ConstraintViolationException, CreateNewServiceException {
        try {
            System.out.println("ejb.session.stateless.ServiceSessionBean.createNewService()");
            List<Tag> tagsToAssociate = new ArrayList<>();

            for (Long tagId : tagIds) {
                tagsToAssociate.add(tagSessionBeanLocal.retrieveTagByTagId(tagId));
            }

            //A Service must have at least 1 tag
            if (tagsToAssociate.isEmpty()) {
                throw new CreateNewServiceException("Please select a tag!");
            }

            Country countryToAssociate = countrySessionBeanLocal.retrieveCountryByCountryId(countryId);
            Business businessToAssociate = businessSessionBeanLocal.retrieveBusinessById(businessId);

            //set country and tags
            newService.setCountry(countryToAssociate);
            newService.setTags(tagsToAssociate);
            newService.setBusiness(businessToAssociate);

            em.persist(newService);
            em.flush();
            //add service to existing tags and country
            for (Tag tag : tagsToAssociate) {
                tag.getServices().add(newService);
            }
            countryToAssociate.getServices().add(newService);
            businessToAssociate.getServices().add(newService);
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
        } catch (CountryNotFoundException | AccountNotFoundException | TagNotFoundException ex) {
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
    public List<Service> retrieveAllEntertainment() {
        Query query = em.createQuery("SELECT s FROM Service s WHERE s.active = true AND s.serviceType = :st");
        query.setParameter("st", ServiceType.ENTERTAINMENT);
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
    public List<Service> retrieveAllServiceByCountry(Long countryId) {
        Country country = em.find(Country.class, countryId);
        Query query = em.createQuery("SELECT s FROM Service s WHERE s.country = :country");
        query.setParameter("country", country);

        return query.getResultList();
    }

    @Override
    public List<Service> retrieveAllActiveServiceByCountry(Long countryId) {
        Country country = em.find(Country.class, countryId);
        Query query = em.createQuery("SELECT s FROM Service s WHERE s.active = true AND s.country = :country");
        query.setParameter("country", country);

        return query.getResultList();
    }

    @Override
    public List<Service> retrieveAllActiveServiceByTags(List<Long> tagIds) {

        List<Service> services = new ArrayList<>();

        if (tagIds == null || tagIds.isEmpty()) {
            return services;
        } else {
            Query query = em.createQuery("SELECT DISTINCT s FROM Service s, IN (s.tags) t WHERE t.tagId IN :inTagIds and s.active = true");
            query.setParameter("inTagIds", tagIds);
            return query.getResultList();
        }
    }

    @Override
    public List<Service> retrieveAllServiceByBusinessId(Long businessId) {
        Query query = em.createQuery("SELECT s FROM Service s WHERE s.business.accountId = :inBusiness");
        query.setParameter("inBusiness", businessId);
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
    public List<Service> retrieveAllActiveServiceByBusinessId(Long businessId) {
        Query query = em.createQuery("SELECT s FROM Service s WHERE s.active = true AND s.business.accountId = :inBusiness");
        query.setParameter("inBusiness", businessId);
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
        System.out.println("ejb.session.stateless.ServiceSessionBean.updateService()");
        if (newService != null && newService.getServiceId() != null) {
            Service serviceToUpdate = this.retrieveServiceById(newService.getServiceId());

            serviceToUpdate.setCountry(newService.getCountry());
            serviceToUpdate.setRates(newService.getRates());
            serviceToUpdate.setServiceType(newService.getServiceType());
            serviceToUpdate.setAddress(newService.getAddress());
            serviceToUpdate.setRequireVaccination(newService.getRequireVaccination());
            serviceToUpdate.setTags(newService.getTags());
            serviceToUpdate.setServiceName(newService.getServiceName());
            em.flush();
        } else {
            throw new AccountNotFoundException("Service ID not provided for service to be updated");
        }
    }

    // Implemented deactivation of service instead of deletion to preserve Booking records
    @Override
    public void toggleServiceActivation(Long serviceId) throws ServiceNotFoundException {
        Service service = this.retrieveServiceById(serviceId);
        if (service != null && service.getServiceId() != null) {
            Boolean newStatus = service.getActive() ? false : true;
            service.setActive(newStatus);
        } else {
            throw new ServiceNotFoundException("ID not provided for account status to be updated");
        }
    }

    @Override
    public Service updateService(Service selectedService, List<Long> tagsSelected, Long selectedCountry, Boolean requireVac) throws UpdateServiceException, AccountNotFoundException, TagNotFoundException, ServiceNotFoundException, CountryNotFoundException {
        System.out.println("ejb.session.stateless.ServiceSessionBean.updateService()");
        Service serviceToUpdate = retrieveServiceById(selectedService.getServiceId());
        serviceToUpdate.setServiceType(selectedService.getServiceType());
        List<Tag> tags = new ArrayList<>();
        for (Long t : tagsSelected) {
            tags.add(tagSessionBeanLocal.retrieveTagByTagId(t));
        }
        serviceToUpdate.setTags(tags);
        serviceToUpdate.setCountry(countrySessionBeanLocal.retrieveCountryByCountryId(selectedCountry));
        serviceToUpdate.setRequireVaccination(requireVac);

        updateService(selectedService);
        return retrieveServiceById(serviceToUpdate.getServiceId());

    }
}

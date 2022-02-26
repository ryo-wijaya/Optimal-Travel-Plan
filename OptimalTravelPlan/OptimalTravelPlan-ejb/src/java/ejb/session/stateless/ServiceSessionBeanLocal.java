/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Service;
import java.util.List;
import javax.ejb.Local;
import util.exception.AccountNotFoundException;
import util.exception.ConstraintViolationException;
import util.exception.CreateNewServiceException;
import util.exception.ServiceNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateServiceException;

/**
 *
 * @author sucram
 */
@Local
public interface ServiceSessionBeanLocal {

    public Long createNewService(Service newService, Long businessId, List<Long> tagIds, Long countryId) throws UnknownPersistenceException, ConstraintViolationException, CreateNewServiceException;

    public Service retrieveServiceById(Long serviceId) throws ServiceNotFoundException;

    public List<Service> retrieveAllServices();

    public List<Service> retrieveAllActiveServices();

    public void updateService(Service newService) throws ServiceNotFoundException, UpdateServiceException, AccountNotFoundException;

    public void toggleServiceActivation(Long serviceId) throws ServiceNotFoundException;

    public List<Service> retrieveAllServiceByCountry(Long countryId);
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ServiceRate;
import java.util.List;
import javax.ejb.Local;
import util.exception.ConstraintViolationException;
import util.exception.CreateNewServiceRateException;
import util.exception.ServiceNotFoundException;
import util.exception.ServiceRateNotFoundException;
import util.exception.UnknownPersistenceException;

@Local
public interface ServiceRateSessionBeanLocal {

    public ServiceRate updateServiceRate(ServiceRate serviceRate) throws ServiceRateNotFoundException, ConstraintViolationException;
    
    public ServiceRate retrieveServiceRateById(Long serviceRateId) throws ServiceRateNotFoundException;

    public Long createNewServiceRate(ServiceRate newServiceRate, Long serviceId) throws UnknownPersistenceException, ConstraintViolationException, CreateNewServiceRateException;

    public void toggleServiceRateActivation(Long serviceRateId) throws ServiceRateNotFoundException;
    
    public List<ServiceRate> retrieveServiceRateByServiceId(Long serviceID) throws ServiceNotFoundException;
}

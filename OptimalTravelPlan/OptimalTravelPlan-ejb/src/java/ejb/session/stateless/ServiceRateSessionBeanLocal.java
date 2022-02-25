/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ServiceRate;
import javax.ejb.Local;
import util.exception.ConstraintViolationException;
import util.exception.CreateNewServiceRateException;
import util.exception.ServiceRateNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author sucram
 */
@Local
public interface ServiceRateSessionBeanLocal {

    public ServiceRate retrieveServiceRateById(Long serviceRateId) throws ServiceRateNotFoundException;

    public Long createNewServiceRate(ServiceRate newServiceRate, Long serviceId) throws UnknownPersistenceException, ConstraintViolationException, CreateNewServiceRateException;

    public void toggleServiceRateActivation(Long serviceRateId) throws ServiceRateNotFoundException;
    
}

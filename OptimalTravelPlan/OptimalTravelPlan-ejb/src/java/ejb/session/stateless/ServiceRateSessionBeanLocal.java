/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ServiceRate;
import javax.ejb.Local;
import util.exception.ServiceRateNotFoundException;

/**
 *
 * @author sucram
 */
@Local
public interface ServiceRateSessionBeanLocal {

    public ServiceRate retrieveServiceRateById(Long serviceRateId) throws ServiceRateNotFoundException;
    
}

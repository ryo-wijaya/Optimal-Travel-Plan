/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ServiceRate;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.ServiceRateNotFoundException;

@Stateless
public class ServiceRateSessionBean implements ServiceRateSessionBeanLocal {

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;
    
    //public Long createNewServiceRate(Long serviceRateId)
    
    @Override
    public ServiceRate retrieveServiceRateById(Long serviceRateId) throws ServiceRateNotFoundException {
        ServiceRate serviceRate = em.find(ServiceRate.class, serviceRateId);
        if (serviceRate != null) {
            return serviceRate;
        } else {
            throw new ServiceRateNotFoundException();
        }
    }
}

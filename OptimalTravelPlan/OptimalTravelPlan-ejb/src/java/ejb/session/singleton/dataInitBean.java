/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import entity.Staff;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.EmployeeRole;

@Singleton
@LocalBean
@Startup
public class dataInitBean {

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;

    public dataInitBean() {
    }
    
    @PostConstruct
    public void postConstruct(){
        if(em.createQuery("SELECT s FROM Staff s").getResultList().size() < 1){
            //default manager
            Staff manager = new Staff("manager", "password", "admin1", EmployeeRole.ADMINISTRATOR);
            em.persist(manager);
            
            //Create data here
            
            
            em.flush();
        }
    }
}

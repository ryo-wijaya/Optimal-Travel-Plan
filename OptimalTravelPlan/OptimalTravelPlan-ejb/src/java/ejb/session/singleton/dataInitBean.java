/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.CountrySessionBeanLocal;
import ejb.session.stateless.ServiceRateSessionBeanLocal;
import ejb.session.stateless.ServiceSessionBeanLocal;
import ejb.session.stateless.TagSessionBeanLocal;
import entity.Business;
import entity.Country;
import entity.Service;
import entity.ServiceRate;
import entity.Staff;
import entity.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.EmployeeRole;
import util.enumeration.ServiceType;
import util.exception.ConstraintViolationException;
import util.exception.CreateNewServiceException;
import util.exception.UnknownPersistenceException;

@Singleton
@LocalBean
@Startup
public class dataInitBean {

    @EJB
    private ServiceRateSessionBeanLocal serviceRateSessionBeanLocal;

    @EJB
    private ServiceSessionBeanLocal serviceSessionBeanLocal;

    @EJB
    private CountrySessionBeanLocal countrySessionBeanLocal;

    @EJB
    private TagSessionBeanLocal tagSessionBeanLocal;

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
            em.flush();
            
            Staff customerService = new Staff("staff", "password", "staff1", EmployeeRole.CUSTOMER_SERVICE);
            em.persist(customerService);
            em.flush();
            
            Business business1 = new Business("company1", "www.company1.com", "0000001", "address1", "company1", "password");
            em.persist(business1);
            em.flush();
            
            Business business2 = new Business("company2", "www.company2.com", "0000002", "address2", "company2", "password");
            em.persist(business2);
            em.flush();
            
            Business business3 = new Business("company3", "www.company3.com", "0000003", "address3", "company3", "password");
            em.persist(business3);
            em.flush();
            
            Business business4 = new Business("company4", "www.company4.com", "0000004", "address4", "company4", "password");
            em.persist(business4);
            em.flush();
            
            Business business5 = new Business("company5", "www.company5.com", "0000005", "address5", "company5", "password");
            em.persist(business5);
            em.flush();
            
            //Create data here
            Tag familyTag = tagSessionBeanLocal.createNewTag(new Tag("family"));
            Tag natureTag = tagSessionBeanLocal.createNewTag(new Tag("nature"));
            Tag cultureTag = tagSessionBeanLocal.createNewTag(new Tag("culture"));
            Tag nightTag = tagSessionBeanLocal.createNewTag(new Tag("night"));
            
            List<Long> tagList1 = new ArrayList<>();
            tagList1.add(familyTag.getTagId());
            
            Country singapore = countrySessionBeanLocal.createNewCountry(new Country("Singapore"));
            Country japan = countrySessionBeanLocal.createNewCountry(new Country("Japan"));
            Country Taiwan = countrySessionBeanLocal.createNewCountry(new Country("Taiwan"));
            
            try {
                Long service1 = serviceSessionBeanLocal.createNewService(new Service(business1, singapore, ServiceType.HOTEL, Boolean.TRUE, "address1"), business1.getBusinessId(), tagList1, singapore.getCountryId());
                
            } catch (UnknownPersistenceException | ConstraintViolationException | CreateNewServiceException ex) {
                Logger.getLogger(dataInitBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            em.flush();
        }
    }
}

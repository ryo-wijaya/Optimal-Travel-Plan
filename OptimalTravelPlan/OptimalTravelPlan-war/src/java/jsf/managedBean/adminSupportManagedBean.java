/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import ejb.session.stateless.SupportRequestSessionBeanLocal;
import entity.SupportRequest;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;

/**
 *
 * @author Jorda
 */
@Named(value = "adminSupportManagedBean")
@ViewScoped
public class adminSupportManagedBean implements Serializable {

    @EJB
    private SupportRequestSessionBeanLocal supportRequestSessionBeanLocal;

    private List<SupportRequest> supportRequests;
    private List<SupportRequest> filteredSupportRequests;
    private List<SupportRequest> unresolvedSupportRequests;
    
    private SupportRequest supportRequest;
    
    /**
     * Creates a new instance of adminSupportManagedBean
     */
    public adminSupportManagedBean() {
    }
    
    @PostConstruct
    public void postConstruct() {
        supportRequests = supportRequestSessionBeanLocal.retrieveAllSupportRequests();
        unresolvedSupportRequests = supportRequestSessionBeanLocal.retrieveAllUnresolvedSupportRequests();
    }
    
    
    
}

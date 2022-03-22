/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import ejb.session.stateless.ServiceSessionBeanLocal;
import entity.Service;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

/**
 *
 * @author sucram
 */
@Named(value = "serviceManagementManagedBean")
@ViewScoped
public class serviceManagementManagedBean implements Serializable {

    @EJB(name = "ServiceSessionBeanLocal")
    private ServiceSessionBeanLocal serviceSessionBeanLocal;

    private List<Service> services;
    private List<Service> filteredServices;

    public serviceManagementManagedBean() {
    }

    @PostConstruct
    public void post() {
        filteredServices = (List<Service>) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("servicesToView");
        services = serviceSessionBeanLocal.retrieveAllActiveServices();
    }

}

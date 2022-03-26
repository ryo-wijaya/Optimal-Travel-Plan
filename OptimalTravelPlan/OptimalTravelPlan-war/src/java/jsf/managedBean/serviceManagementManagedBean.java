/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import ejb.session.stateless.ServiceSessionBeanLocal;
import entity.Business;
import entity.Service;
import java.io.IOException;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

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
    private Boolean filtered;

    public serviceManagementManagedBean() {
    }

    @PostConstruct
    public void post() {
        List<Service> list = (List<Service>) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("servicesToView");
        if (list != null) {
            services = list;
            filtered = true;
        } else {
            services = serviceSessionBeanLocal.retrieveAllActiveServices();
            filtered = false;
        }
        System.out.println("list = " + list);
        System.out.println("Services = " + services);
    }

    public void refreshServicesList(ActionEvent event) {
        this.services = serviceSessionBeanLocal.retrieveAllServices();
        this.filtered = false;
    }

    public void viewServiceOwner(ActionEvent event) throws IOException {
        Business business = (Business) event.getComponent().getAttributes().remove("business");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("businessToView", business);
        FacesContext.getCurrentInstance().getExternalContext().redirect("BusinessManagement.xhtml");
    }

    public void toggleServiceActive(ActionEvent event) {
        Service service = (Service) event.getComponent().getAttributes().remove("service");
        Boolean temp = service.getActive();
        if (temp){
            service.setActive(false);
        } else{
            service.setActive(true);
        }
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public List<Service> getFilteredServices() {
        return filteredServices;
    }

    public void setFilteredServices(List<Service> filteredServices) {
        this.filteredServices = filteredServices;
    }

    public Boolean getFiltered() {
        return filtered;
    }

    public void setFiltered(Boolean filtered) {
        this.filtered = filtered;
    }
}

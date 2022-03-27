/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import ejb.session.stateless.CountrySessionBeanLocal;
import ejb.session.stateless.ServiceSessionBeanLocal;
import ejb.session.stateless.TagSessionBeanLocal;
import entity.Business;
import entity.Country;
import entity.Service;
import entity.Tag;
import java.io.IOException;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import util.enumeration.ServiceType;

/**
 *
 * @author sucram
 */
@Named(value = "serviceManagementManagedBean")
@ViewScoped
public class serviceManagementManagedBean implements Serializable {

    @EJB(name = "CountrySessionBeanLocal")
    private CountrySessionBeanLocal countrySessionBeanLocal;

    @EJB(name = "TagSessionBeanLocal")
    private TagSessionBeanLocal tagSessionBeanLocal;

    @EJB(name = "ServiceSessionBeanLocal")
    private ServiceSessionBeanLocal serviceSessionBeanLocal;



    private List<Service> services;
    private List<Service> filteredServices;
    private Boolean filtered;
    private Service newService;
    private List<Long> tagsSelected;
    private List<Tag> allTags;
    private List<Country> allCountries;
    private Long selectedCountry;
    private Boolean requireVac;


    public serviceManagementManagedBean() {

    }

    @PostConstruct
    public void post() {
        List<Service> list = (List<Service>) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("servicesToView");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("servicesToView");
        if (list != null) {
            services = list;
            filtered = true;
        } else {
            refreshServicesList(null);
        }
        this.allCountries = countrySessionBeanLocal.retrieveAllCountries();
        this.allTags = tagSessionBeanLocal.retrieveAllTags();
        this.tagsSelected = new ArrayList<>();
        this.newService = new Service();
    }

    public void refreshServicesList(ActionEvent event) {
        this.services = serviceSessionBeanLocal.retrieveAllServices();
        this.filtered = false;
    }

    public void viewServiceOwner(ActionEvent event) throws IOException {
        Business business = (Business) event.getComponent().getAttributes().get("businessToView");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("businessToView", business);
        FacesContext.getCurrentInstance().getExternalContext().redirect("BusinessManagement.xhtml");
    }

    public void toggleServiceActive(ActionEvent event) {
        Service service = (Service) event.getComponent().getAttributes().get("serviceToToggle");
        Boolean temp = service.getActive();
        if (temp) {
            service.setActive(false);
        } else {
            service.setActive(true);
        }
    }

    public void createNewNonBusinessService(ActionEvent event) {
        try {
            if (selectedCountry == null) {
                throw new Exception("Please Select a country!");
            }
            if (tagsSelected == null || tagsSelected.size() < 1) {
                throw new Exception("Please select at least 1 tag!");
            }
            newService.setRequireVaccination(requireVac);
            newService.setServiceType(ServiceType.ENTERTAINMENT);
            Long s = serviceSessionBeanLocal.createNewService(newService, 1l, tagsSelected, selectedCountry);
            newService.setServiceId(s);
            this.services.add(newService);
            newService = new Service();
            selectedCountry = null;
            tagsSelected = null;
            requireVac = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully Created service ID = " + s, null));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new product: " + e.getMessage(), null));
        }
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public Boolean getRequireVac() {
        return requireVac;
    }

    public void setRequireVac(Boolean requireVac) {
        this.requireVac = requireVac;
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

    public Service getNewService() {
        return newService;
    }

    public void setNewService(Service newService) {
        this.newService = newService;
    }

    public List<Long> getTagsSelected() {
        return tagsSelected;
    }

    public void setTagsSelected(List<Long> tagsSelected) {
        this.tagsSelected = tagsSelected;
    }

    public List<Tag> getAllTags() {
        return allTags;
    }

    public void setAllTags(List<Tag> allTags) {
        this.allTags = allTags;
    }

    public List<Country> getAllCountries() {
        return allCountries;
    }

    public void setAllCountries(List<Country> allCountries) {
        this.allCountries = allCountries;
    }

    public Long getSelectedCountry() {
        return selectedCountry;
    }

    public void setSelectedCountry(Long selectedCountry) {
        this.selectedCountry = selectedCountry;
    }
}

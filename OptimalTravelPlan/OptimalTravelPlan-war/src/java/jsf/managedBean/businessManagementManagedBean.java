/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import ejb.session.stateless.AccountSessionBeanLocal;
import ejb.session.stateless.BusinessSessionBeanLocal;
import entity.Business;
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
import org.primefaces.PrimeFaces;
import util.exception.AccountNotFoundException;
import util.exception.DeleteBusinessException;
import util.exception.BusinessAlreadyExistException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateBusinessException;
import util.exception.UsernameAlreadyExistException;

/**
 *
 * @author Anais
 */
@Named(value = "businessManagementManagedBean")
@ViewScoped
public class businessManagementManagedBean implements Serializable {

    @EJB
    private BusinessSessionBeanLocal businessSessionBeanLocal;
    private AccountSessionBeanLocal accountSessionBeanLocal;

    private List<Business> businesses;
    private List<Business> filteredBusinesses;
    private Boolean filtered;
    private Business newBusiness;
    private Business businessToUpdate;

    public businessManagementManagedBean() {
        newBusiness = new Business();
    }

    @PostConstruct
    public void post() {
        List<Business> selectedBusinesses = (List<Business>) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("businessesToView");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("businessesToView");
        if (selectedBusinesses == null) {
            businesses = businessSessionBeanLocal.retrieveAllBusinesses();
            filtered = false;
        } else {
            businesses = selectedBusinesses;
            filtered = true;
        }
        Boolean addBusiness = (Boolean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("addNewBusiness");
        if (addBusiness != null && addBusiness) {
            PrimeFaces.current().executeScript("PF('dialogCreateNewBusiness').show();");
        }
    }

    public void refreshBusinessesList(ActionEvent event) {
        this.businesses = businessSessionBeanLocal.retrieveAllBusinesses();
        this.filtered = false;
    }

    public void createNewBusiness(ActionEvent event) throws UsernameAlreadyExistException, UnknownPersistenceException, AccountNotFoundException {
        Business t = businessSessionBeanLocal.retrieveBusinessById(accountSessionBeanLocal.createNewAccount(getNewBusiness()));
        businesses.add(t);
        newBusiness = new Business();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New business created successfully (Business ID: " + t.getBusinessId() + ")", null));
    }

    public void viewBusinessServices(ActionEvent event) throws IOException {
        Business selectedBusiness = (Business) event.getComponent().getAttributes().get("businessToViewServices");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("servicesToView", selectedBusiness.getServices());
        FacesContext.getCurrentInstance().getExternalContext().redirect("serviceManagement.xhtml");
    }

    public void doUpdateBusiness(ActionEvent event) {
        businessToUpdate = (Business) event.getComponent().getAttributes().get("businessToUpdate");
    }

    public void updateBusiness(ActionEvent event) throws AccountNotFoundException, UpdateBusinessException {
        businessSessionBeanLocal.updateBusiness(businessToUpdate);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Business updated successfully", null));
    }

    public void deleteBusiness(ActionEvent event) throws AccountNotFoundException {
        try {
            Business businessToDelete = (Business) event.getComponent().getAttributes().get("businessToDelete");
            businessSessionBeanLocal.deleteBusiness(businessToDelete.getBusinessId());
            businesses.remove(businessToDelete);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Business deleted successfully", null));
        } catch (DeleteBusinessException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Unable to delete:" + ex.getMessage(), null));
        }
    }

    public List<Business> getBusinesses() {
        return businesses;
    }

    public void setBusinesses(List<Business> businesses) {
        this.businesses = businesses;
    }

    public List<Business> getFilteredBusinesses() {
        return filteredBusinesses;
    }

    public void setFilteredBusinesses(List<Business> filteredBusinesses) {
        this.filteredBusinesses = filteredBusinesses;
    }

    public Business getNewBusiness() {
        return newBusiness;
    }

    public void setNewBusiness(Business newBusiness) {
        this.newBusiness = newBusiness;
    }

    public Business getBusinessToUpdate() {
        return businessToUpdate;
    }

    public void setBusinessToUpdate(Business businessToUpdate) {
        this.businessToUpdate = businessToUpdate;
    }

    public Boolean getFiltered() {
        return filtered;
    }

    public void setFiltered(Boolean filtered) {
        this.filtered = filtered;
    }
}

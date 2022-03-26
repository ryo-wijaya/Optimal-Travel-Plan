/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import ejb.session.stateless.AccountSessionBeanLocal;
import ejb.session.stateless.BusinessSessionBeanLocal;
import entity.Business;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import util.exception.AccountNotFoundException;
import util.exception.DeleteBusinessException;
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

    private List<Business> businesses;
    private List<Business> filteredBusinesses;
    
    private Business newBusiness;
    
    private Business selectedBusinessToUpdate;
    
    
    /**
     * Creates a new instance of businessManagementManagedBean
     */
    public businessManagementManagedBean() {
        newBusiness = new Business();
    }
    
    @PostConstruct
    public void postConstruct() {
        setBusinesses(businessSessionBeanLocal.retrieveAllBusinesses());
    }
    
    public void createNewBusiness(ActionEvent event) throws UsernameAlreadyExistException, UnknownPersistenceException, AccountNotFoundException {
        Business c = BusinessSessionBeanLocal.retrieveBusinessById(AccountSessionBeanLocal.createNewAccount(getNewBusiness()));
        getBusinesses().add(c);
        setNewBusiness(new Business());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New Business created successfully (Business: " + c.getName() + ")", null));
    }
    
    public void doUpdateBusiness(ActionEvent event) {
        setSelectedBusinessToUpdate((Business) event.getComponent().getAttributes().get("selectedBusinessToUpdate"));
    }
    
    public void updateBusiness(ActionEvent event) throws AccountNotFoundException, UpdateBusinessException {
        businessSessionBeanLocal.updateBusiness(getNewBusiness());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Business updated successfully", null));
    }
    
    public void deleteBusiness(ActionEvent event) {
        try {
            Business businessToDelete = (Business) event.getComponent().getAttributes().get("businessToDelete");
            businessSessionBeanLocal.deleteBusiness(businessToDelete.getBusinessId());
            getBusinesses().remove(businessToDelete);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Business deleted successfully", null));
        } catch (DeleteBusinessException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Unable to delete:" + ex.getMessage(), null));
        }
    }

    /**
     * @return the businesses
     */
    public List<Business> getBusinesses() {
        return businesses;
    }

    /**
     * @param businesses the businesses to set
     */
    public void setBusinesses(List<Business> businesses) {
        this.businesses = businesses;
    }

    /**
     * @return the filteredBusinesses
     */
    public List<Business> getFilteredBusinesses() {
        return filteredBusinesses;
    }

    /**
     * @param filteredBusinesses the filteredBusinesses to set
     */
    public void setFilteredBusinesses(List<Business> filteredBusinesses) {
        this.filteredBusinesses = filteredBusinesses;
    }

    /**
     * @return the newBusiness
     */
    public Business getNewBusiness() {
        return newBusiness;
    }

    /**
     * @param newBusiness the newBusiness to set
     */
    public void setNewBusiness(Business newBusiness) {
        this.newBusiness = newBusiness;
    }

    /**
     * @return the selectedBusinessToUpdate
     */
    public Business getSelectedBusinessToUpdate() {
        return selectedBusinessToUpdate;
    }

    /**
     * @param selectedBusinessToUpdate the selectedBusinessToUpdate to set
     */
    public void setSelectedBusinessToUpdate(Business selectedBusinessToUpdate) {
        this.selectedBusinessToUpdate = selectedBusinessToUpdate;
    }
    
}

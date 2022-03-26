/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import ejb.session.stateless.AccountSessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import entity.Customer;
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
import util.exception.DeleteCustomerException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateCustomerException;
import util.exception.UsernameAlreadyExistException;

/**
 *
 * @author Anais
 */
@Named(value = "customerManagementManagedBean")
@ViewScoped
public class customerManagementManagedBean implements Serializable {

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    private List<Customer> customers;
    private List<Customer> filteredCustomers;
    
    private Customer newCustomer;
    
    private Customer selectedCustomerToUpdate;
    
    
    /**
     * Creates a new instance of customerManagementManagedBean
     */
    public customerManagementManagedBean() {
        newCustomer = new Customer();
    }
    
    @PostConstruct
    public void postConstruct() {
        setCustomers(customerSessionBeanLocal.retrieveAllCustomers());
    }
    
    public void createNewCustomer(ActionEvent event) throws UsernameAlreadyExistException, UnknownPersistenceException, AccountNotFoundException {
        Customer c = CustomerSessionBeanLocal.retrieveCustomerById(AccountSessionBeanLocal.createNewAccount(getNewCustomer()));
        getCustomers().add(c);
        setNewCustomer(new Customer());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New Customer created successfully (Customer: " + c.getName() + ")", null));
    }
    
    public void doUpdateCustomer(ActionEvent event) {
        setSelectedCustomerToUpdate((Customer) event.getComponent().getAttributes().get("selectedCustomerToUpdate"));
    }
    
    public void updateCustomer(ActionEvent event) throws AccountNotFoundException, UpdateCustomerException {
        customerSessionBeanLocal.updateCustomer(getNewCustomer());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Customer updated successfully", null));
    }
    
    public void deleteCustomer(ActionEvent event) {
        try {
            Customer customerToDelete = (Customer) event.getComponent().getAttributes().get("customerToDelete");
            customerSessionBeanLocal.deleteCustomer(customerToDelete.getCustomerId());
            getCustomers().remove(customerToDelete);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Customer deleted successfully", null));
        } catch (DeleteCustomerException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Unable to delete:" + ex.getMessage(), null));
        }
    }

    /**
     * @return the customers
     */
    public List<Customer> getCustomers() {
        return customers;
    }

    /**
     * @param customers the customers to set
     */
    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    /**
     * @return the filteredCustomers
     */
    public List<Customer> getFilteredCustomers() {
        return filteredCustomers;
    }

    /**
     * @param filteredCustomers the filteredCustomers to set
     */
    public void setFilteredCustomers(List<Customer> filteredCustomers) {
        this.filteredCustomers = filteredCustomers;
    }

    /**
     * @return the newCustomer
     */
    public Customer getNewCustomer() {
        return newCustomer;
    }

    /**
     * @param newCustomer the newCustomer to set
     */
    public void setNewCustomer(Customer newCustomer) {
        this.newCustomer = newCustomer;
    }

    /**
     * @return the selectedCustomerToUpdate
     */
    public Customer getSelectedCustomerToUpdate() {
        return selectedCustomerToUpdate;
    }

    /**
     * @param selectedCustomerToUpdate the selectedCustomerToUpdate to set
     */
    public void setSelectedCustomerToUpdate(Customer selectedCustomerToUpdate) {
        this.selectedCustomerToUpdate = selectedCustomerToUpdate;
    }
    
}

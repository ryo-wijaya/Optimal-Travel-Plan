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
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.PrimeFaces;
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
    @EJB
    private AccountSessionBeanLocal accountSessionBeanLocal;

    private List<Customer> customers;
    private List<Customer> filteredCustomers;
    private Boolean filtered;
    private Customer newCustomer;
    private Customer customerToUpdate;
    private Customer selectedCustomer;
    
    //private List<PaymentAccount> selectedPaymentAccounts;

    public customerManagementManagedBean() {
        newCustomer = new Customer();
        //selectedPaymentAccounts = new ArrayList<PaymentAccount> ();
    }

    @PostConstruct
    public void post() {
        List<Customer> selectedCustomers = (List<Customer>) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("customersToView");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("customersToView");
        if (selectedCustomers == null) {
            customers = customerSessionBeanLocal.retrieveAllCustomers();
            filtered = false;
        } else {
            customers = selectedCustomers;
            filtered = true;
        }
        Boolean addCustomer = (Boolean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("addNewCustomer");
        if (addCustomer != null && addCustomer) {
            PrimeFaces.current().executeScript("PF('dialogCreateNewCustomer').show();");
        }
    }

    public void refreshCustomersList(ActionEvent event) {
        this.customers = customerSessionBeanLocal.retrieveAllCustomers();
        this.filtered = false;
    }

    public void createNewCustomer(ActionEvent event) throws UsernameAlreadyExistException, UnknownPersistenceException, AccountNotFoundException {
        Customer t = customerSessionBeanLocal.retrieveCustomerById(accountSessionBeanLocal.createNewAccount(getNewCustomer()));
        customers.add(t);
        newCustomer = new Customer();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New customer created successfully (Customer ID: " + t.getCustomerId() + ")", null));
    }

    public void doUpdateCustomer(ActionEvent event) {
        customerToUpdate = (Customer) event.getComponent().getAttributes().get("customerToUpdate");
    }

    public void updateCustomer(ActionEvent event) throws AccountNotFoundException, UpdateCustomerException {
        customerSessionBeanLocal.updateCustomer(customerToUpdate);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Customer updated successfully", null));
    }

    public void deleteCustomer(ActionEvent event) throws AccountNotFoundException {
        try {
            Customer customerToDelete = (Customer) event.getComponent().getAttributes().get("customerToDelete");
            Boolean new1 = customerToDelete.getEnabled()? false: true;
            customerToDelete.setEnabled(new1);
            String ms = "";
            if(new1){
                ms = "Active";
            } else{
                ms = "Disabled";
            }
            System.out.println("jsf.managedBean.customerManagementManagedBean.deleteCustomer() Customer id =" + customerToDelete.getCustomerId());
            customerSessionBeanLocal.deleteCustomer(customerToDelete.getCustomerId());
//            for( Customer c: this.customers){
//                if(c.getCustomerId().equals(customerToDelete.getAccountId())){
//                    System.out.println("jsf.managedBean.customerManagementManagedBean.deleteCustomer() found customer to update updated to " + customerToDelete.getEnabled());
//                    c.setEnabled(customerToDelete.getEnabled());
//                }
//            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Customer account has be set to " + ms, null));
        } catch (DeleteCustomerException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Unable to delete:" + ex.getMessage(), null));
        }
    }
    
//    public void viewPaymentAccounts() {
//        System.out.println(selectedPaymentAccounts.isEmpty());
//    }

    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }
    
    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public List<Customer> getFilteredCustomers() {
        return filteredCustomers;
    }

    public void setFilteredCustomers(List<Customer> filteredCustomers) {
        this.filteredCustomers = filteredCustomers;
    }

    public Customer getNewCustomer() {
        return newCustomer;
    }

    public void setNewCustomer(Customer newCustomer) {
        this.newCustomer = newCustomer;
    }

    public Customer getCustomerToUpdate() {
        return customerToUpdate;
    }

    public void setCustomerToUpdate(Customer customerToUpdate) {
        this.customerToUpdate = customerToUpdate;
    }

    public Boolean getFiltered() {
        return filtered;
    }

    public void setFiltered(Boolean filtered) {
        this.filtered = filtered;
    }

//    public List<PaymentAccount> getSelectedPaymentAccounts() {
//        return selectedPaymentAccounts;
//    }
//
//    public void setSelectedPaymentAccounts(List<PaymentAccount> selectedPaymentAccounts) {
//        this.selectedPaymentAccounts = selectedPaymentAccounts;
//    }
}

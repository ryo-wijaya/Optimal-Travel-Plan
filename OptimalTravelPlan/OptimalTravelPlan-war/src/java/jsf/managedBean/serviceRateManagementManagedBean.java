package jsf.managedBean;

import ejb.session.stateless.ServiceRateSessionBeanLocal;
import entity.Account;
import entity.Service;
import entity.ServiceRate;
import java.io.IOException;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.PrimeFaces;
import util.enumeration.ChargeType;
import util.enumeration.RateType;
import util.exception.ServiceNotFoundException;

@Named(value = "serviceRateManagementManagedBean")
@ViewScoped
public class serviceRateManagementManagedBean implements Serializable {

    @EJB(name = "ServiceRateSessionBeanLocal")
    private ServiceRateSessionBeanLocal serviceRateSessionBeanLocal;

    private Account loggedInAccount;
    private List<ServiceRate> rates;
    private ServiceRate newServiceRate;
    private Service selectedService;
    private ServiceRate serviceRateToUpdate;
    private List<ServiceRate> filteredServiceRates;
    private List<RateType> allRateTypes;
    private List<ChargeType> allChargeTypes;
    private RateType selectedRateType;
    private ChargeType selectedChargeType;
    private List<Date> invalidDays;

    public serviceRateManagementManagedBean() {
        serviceRateToUpdate = new ServiceRate();
        newServiceRate = new ServiceRate();
        newServiceRate.setRateType(RateType.NORMAL);
        this.allChargeTypes = new ArrayList<>();
        this.allRateTypes = new ArrayList<>();
        this.allChargeTypes.add(ChargeType.ENTRY);
        this.allChargeTypes.add(ChargeType.HOURLY);
        this.allRateTypes.add(RateType.NORMAL);
        this.allRateTypes.add(RateType.PEAK);
        this.allRateTypes.add(RateType.PROMOTION);
    }

    @PostConstruct
    public void post() {
        selectedService = (Service) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("selectedService");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("selectedService");
        loggedInAccount = (Account) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedInAccount");
        if (selectedService != null) {
            try {
                rates = this.serviceRateSessionBeanLocal.retrieveServiceRateByServiceId(selectedService.getServiceId());
            } catch (ServiceNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No Service Selected! Please return service page!", null));
        }
        Boolean addServiceRate = (Boolean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("addNewServiceRate");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("addNewServiceRate");
        if (addServiceRate != null && addServiceRate) {
            PrimeFaces.current().executeScript("PF('dialogCreateNewServiceRate').show();");
        }
    }

    public void createNewServiceRate(ActionEvent event) {
        try {
            if (newServiceRate.getStartDate() == null) {
                newServiceRate.setStartDate(new Date(0l));
            }
            if (newServiceRate.getEndDate() == null) {
                newServiceRate.setEndDate(new Date(1099, 9, 19));
            }
            if (selectedRateType == null) {
                newServiceRate.setRateType(RateType.NORMAL);
            } else {
                newServiceRate.setRateType(selectedRateType);
            }
            newServiceRate.setChargeType(selectedChargeType);

            Long s = this.serviceRateSessionBeanLocal.createNewServiceRate(newServiceRate, selectedService.getServiceId());
            newServiceRate.setServiceRateId(s);
            rates.add(newServiceRate);
            newServiceRate = new ServiceRate();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New service rate created successfully (Rate ID: " + s + ")", null));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Unable to create rate: " + e.getMessage(), null));
        }
    }

    public void doUpdateServiceRate(ActionEvent event) {
        serviceRateToUpdate = (ServiceRate) event.getComponent().getAttributes().get("serviceRateToUpdate");
        updateInvalidDates();
    }

    public void updateServiceRate(ActionEvent event) {
        try {
            ServiceRate t = serviceRateSessionBeanLocal.updateServiceRate(serviceRateToUpdate);
            updateInvalidDates();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Service Rate updated successfully", null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Update error: " + ex.getMessage(), null));
        }
    }

    private void updateInvalidDates() {
        if ((serviceRateToUpdate.getEndDate().getTime() - serviceRateToUpdate.getStartDate().getTime()) < 315360000000l) {
            this.invalidDays = new ArrayList<>();
            Date pointer = serviceRateToUpdate.getStartDate();
            this.invalidDays.add(pointer);
            pointer = new Date(pointer.getTime() + 86400000l);
            while (pointer.before(serviceRateToUpdate.getEndDate())) {
                this.invalidDays.add(pointer);
                pointer = new Date(pointer.getTime() + 86400000l);
            }
        } else {
            this.invalidDays = new ArrayList<>();
        }
    }

    public void deleteServiceRate(ActionEvent event) {
        try {
            ServiceRate serviceRateToDelete = (ServiceRate) event.getComponent().getAttributes().get("serviceRateToToggle");
            serviceRateSessionBeanLocal.toggleServiceRateActivation(serviceRateToDelete.getServiceRateId());
            Boolean setValue = serviceRateToDelete.getEnabled() ? false : true;
            serviceRateToDelete.setEnabled(setValue);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Rate availability toggled!", null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Unable to toggle rate availability:" + ex.getMessage(), null));
        }
    }

    public Account getLoggedInAccount() {
        return loggedInAccount;
    }

    public void setLoggedInAccount(Account loggedInAccount) {
        this.loggedInAccount = loggedInAccount;
    }

    public List<Date> getInvalidDays() {
        return invalidDays;
    }

    public void setInvalidDays(List<Date> invalidDays) {
        this.invalidDays = invalidDays;
    }

    public List<ServiceRate> getRates() {
        return rates;
    }

    public void setRates(List<ServiceRate> rates) {
        this.rates = rates;
    }

    public ServiceRate getNewServiceRate() {
        return newServiceRate;
    }

    public RateType getSelectedRateType() {
        return selectedRateType;
    }

    public void setSelectedRateType(RateType selectedRateType) {
        this.selectedRateType = selectedRateType;
    }

    public ChargeType getSelectedChargeType() {
        return selectedChargeType;
    }

    public void setSelectedChargeType(ChargeType selectedChargeType) {
        this.selectedChargeType = selectedChargeType;
    }

    public void setNewServiceRate(ServiceRate newServiceRate) {
        this.newServiceRate = newServiceRate;
    }

    public Service getSelectedService() {
        return selectedService;
    }

    public void setSelectedService(Service selectedService) {
        this.selectedService = selectedService;
    }

    public ServiceRate getServiceRateToUpdate() {
        return serviceRateToUpdate;
    }

    public void setServiceRateToUpdate(ServiceRate serviceRateToUpdate) {
        this.serviceRateToUpdate = serviceRateToUpdate;
    }

    public List<ServiceRate> getFilteredServiceRates() {
        return filteredServiceRates;
    }

    public void setFilteredServiceRates(List<ServiceRate> filteredServiceRates) {
        this.filteredServiceRates = filteredServiceRates;
    }

    public List<RateType> getAllRateTypes() {
        return allRateTypes;
    }

    public void setAllRateTypes(List<RateType> allRateTypes) {
        this.allRateTypes = allRateTypes;
    }

    public List<ChargeType> getAllChargeTypes() {
        return allChargeTypes;
    }

    public void setAllChargeTypes(List<ChargeType> allChargeTypes) {
        this.allChargeTypes = allChargeTypes;
    }

}

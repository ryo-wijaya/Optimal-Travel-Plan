/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import ejb.session.stateless.AccountSessionBeanLocal;
import ejb.session.stateless.StaffSessionBeanLocal;
import entity.Staff;
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
import util.exception.DeleteStaffException;
import util.exception.StaffAlreadyExistException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateStaffException;
import util.exception.UsernameAlreadyExistException;

/**
 *
 * @author Anais
 */
@Named(value = "staffManagementManagedBean")
@ViewScoped
public class adminManagementManagedBean implements Serializable {

    @EJB
    private StaffSessionBeanLocal staffSessionBeanLocal;
    private AccountSessionBeanLocal accountSessionBeanLocal;

    private List<Staff> staffs;
    private List<Staff> filteredStaffs;
    private Boolean filtered;
    private Staff newStaff;
    private Staff staffToUpdate;

    public adminManagementManagedBean() {
        newStaff = new Staff();
    }

    @PostConstruct
    public void post() {
        List<Staff> selectedStaffs = (List<Staff>) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("staffsToView");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("staffsToView");
        if (selectedStaffs == null) {
            staffs = staffSessionBeanLocal.retrieveAllStaff();
            filtered = false;
        } else {
            staffs = selectedStaffs;
            filtered = true;
        }
        Boolean addStaff = (Boolean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("addNewStaff");
        if (addStaff != null && addStaff) {
            PrimeFaces.current().executeScript("PF('dialogCreateNewStaff').show();");
        }
    }

    public void refreshStaffsList(ActionEvent event) {
        this.staffs = staffSessionBeanLocal.retrieveAllStaff();
        this.filtered = false;
    }

    public void createNewStaff(ActionEvent event) throws UsernameAlreadyExistException, UnknownPersistenceException, AccountNotFoundException {
        Staff t = staffSessionBeanLocal.retrieveStaffById(accountSessionBeanLocal.createNewAccount(getNewStaff()));
        staffs.add(t);
        newStaff = new Staff();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New staff created successfully (Staff ID: " + t.getStaffId() + ")", null));
    }

    public void doUpdateStaff(ActionEvent event) {
        staffToUpdate = (Staff) event.getComponent().getAttributes().get("staffToUpdate");
    }

    public void updateStaff(ActionEvent event) throws AccountNotFoundException, UpdateStaffException {
        staffSessionBeanLocal.updateStaff(staffToUpdate);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Staff updated successfully", null));
    }

    public void deleteStaff(ActionEvent event) throws AccountNotFoundException {
        try {
            Staff staffToDelete = (Staff) event.getComponent().getAttributes().get("staffToDelete");
            staffSessionBeanLocal.deleteStaff(staffToDelete.getStaffId());
            staffs.remove(staffToDelete);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Staff deleted successfully", null));
        } catch (DeleteStaffException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Unable to delete:" + ex.getMessage(), null));
        }
    }

    public List<Staff> getStaffs() {
        return staffs;
    }

    public void setStaffs(List<Staff> staffs) {
        this.staffs = staffs;
    }

    public List<Staff> getFilteredStaffs() {
        return filteredStaffs;
    }

    public void setFilteredStaffs(List<Staff> filteredStaffs) {
        this.filteredStaffs = filteredStaffs;
    }

    public Staff getNewStaff() {
        return newStaff;
    }

    public void setNewStaff(Staff newStaff) {
        this.newStaff = newStaff;
    }

    public Staff getStaffToUpdate() {
        return staffToUpdate;
    }

    public void setStaffToUpdate(Staff staffToUpdate) {
        this.staffToUpdate = staffToUpdate;
    }

    public Boolean getFiltered() {
        return filtered;
    }

    public void setFiltered(Boolean filtered) {
        this.filtered = filtered;
    }
}

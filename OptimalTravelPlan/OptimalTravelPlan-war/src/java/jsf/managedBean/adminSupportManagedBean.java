/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import ejb.session.stateless.BookingSessionBeanLocal;
import ejb.session.stateless.SupportRequestSessionBeanLocal;
import entity.Booking;
import entity.Staff;
import entity.SupportRequest;
import java.io.IOException;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import util.exception.ResolveSupportRequestException;
import util.exception.SupportRequestNotFoundException;

/**
 *
 * @author Jorda
 */
@Named(value = "adminSupportManagedBean")
@ViewScoped
public class adminSupportManagedBean implements Serializable {

    @EJB
    private BookingSessionBeanLocal bookingSessionBeanLocal;

    @EJB
    private SupportRequestSessionBeanLocal supportRequestSessionBeanLocal;

    private List<SupportRequest> supportRequests;
    private List<SupportRequest> filteredSupportRequests;
    private List<SupportRequest> unresolvedSupportRequests;

    private SupportRequest supportRequestToView;
    private SupportRequest supportRequestToToggle;
    private SupportRequest supportRequestToUpdate;
    private String newComment;
    private Booking booking;

    private Staff admin;

    /**
     * Creates a new instance of adminSupportManagedBean
     */
    public adminSupportManagedBean() {
        supportRequestToView = new SupportRequest();
    }

    @PostConstruct
    public void postConstruct() {
        refreshList();
        this.admin = (Staff) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedInAccount");
    }

    private void refreshList() {
        setUnresolvedSupportRequests(supportRequestSessionBeanLocal.retrieveAllUnresolvedSupportRequests());
        setSupportRequests(supportRequestSessionBeanLocal.retrieveAllSupportRequests());
    }

    public void viewSupportRequestService(ActionEvent event) throws IOException {
        SupportRequest selectedSupportRequest = (SupportRequest) event.getComponent().getAttributes().get("supportRequestToViewService");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("servicesToView", selectedSupportRequest.getBooking().getService());
        FacesContext.getCurrentInstance().getExternalContext().redirect("serviceManagement.xhtml");
    }

    public void viewSupportRequestCustomer(ActionEvent event) throws IOException {
        SupportRequest selectedSupportRequest = (SupportRequest) event.getComponent().getAttributes().get("supportRequestToViewService");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("servicesToView", selectedSupportRequest.getBooking().getTravelItinerary().getCustomer());
        FacesContext.getCurrentInstance().getExternalContext().redirect("customerManagement.xhtml");
    }

    public void viewSupportRequestBusiness(ActionEvent event) throws IOException {
        SupportRequest selectedSupportRequest = (SupportRequest) event.getComponent().getAttributes().get("supportRequestToViewService");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("servicesToView", selectedSupportRequest.getBooking().getService().getBusiness());
        FacesContext.getCurrentInstance().getExternalContext().redirect("businessManagement.xhtml");
    }

    public void doUpdateSupportRequest(ActionEvent event) {
        setSupportRequestToUpdate(this.supportRequestToView);
    }

    public void doViewSupportRequest(ActionEvent event) {
        this.supportRequestToView = (SupportRequest) event.getComponent().getAttributes().get("supportRequestToView");
    }

    public void updateComment(ActionEvent event) {
        try {
            this.supportRequestToUpdate = supportRequestSessionBeanLocal.updateSupportRequestDetails(getSupportRequestToUpdate().getSupportRequestId(), this.admin.getAccountId(), newComment);
            newComment = "";
            refreshList();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Comment updated!", null));
        } catch (SupportRequestNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating comment: " + ex.getMessage(), null));
        }
    }

    public void resolveSupportRequest(ActionEvent event) {
        try {
            supportRequestSessionBeanLocal.resolveSupportRequest(getSupportRequestToUpdate().getSupportRequestId());
            refreshList();
        } catch (SupportRequestNotFoundException | ResolveSupportRequestException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while resolving request: " + ex.getMessage(), null));
        }
    }

    public void toggleSupportRequestResolved() {
        try {
            Boolean temp = supportRequestToToggle.getResolved();
            if (temp) {
                supportRequestToToggle.setResolved(false);
            } else {
                supportRequestToToggle.setResolved(true);
            }
            supportRequestSessionBeanLocal.resolveSupportRequest(supportRequestToToggle.getSupportRequestId());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Marked issue resolved!", null));
            refreshList();
        } catch (SupportRequestNotFoundException | ResolveSupportRequestException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while resolving request: " + ex.getMessage(), null));
        }
    }
    
    /*
    public void toggleSupportRequestResolved(ActionEvent event) {
        try {
            Boolean temp = supportRequestToUpdate.getResolved();
            if (temp) {
                supportRequestToUpdate.setResolved(false);
            } else {
                supportRequestToUpdate.setResolved(true);
            }
            String tes = newComment;
            newComment = "";
            this.supportRequestToUpdate = supportRequestSessionBeanLocal.updateSupportRequestDetails(getSupportRequestToUpdate().getSupportRequestId(), this.admin.getAccountId(), tes);
            supportRequestSessionBeanLocal.resolveSupportRequest(supportRequestToUpdate.getSupportRequestId());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Marked issue resolved!", null));
            refreshList();
        } catch (SupportRequestNotFoundException | ResolveSupportRequestException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while resolving request: " + ex.getMessage(), null));
        }
    }
    */

    public void retrieveBookingByRequest(ActionEvent event) throws SupportRequestNotFoundException {
        this.booking = bookingSessionBeanLocal.retrieveBookingBySupportRequest((Long) event.getComponent().getAttributes().get("serviceId"));
    }

    public List<SupportRequest> getSupportRequests() {
        return supportRequests;
    }

    public void setSupportRequests(List<SupportRequest> supportRequests) {
        this.supportRequests = supportRequests;
    }

    public List<SupportRequest> getFilteredSupportRequests() {
        return filteredSupportRequests;
    }

    public void setFilteredSupportRequests(List<SupportRequest> filteredSupportRequests) {
        this.filteredSupportRequests = filteredSupportRequests;
    }

    public List<SupportRequest> getUnresolvedSupportRequests() {
        return unresolvedSupportRequests;
    }

    public void setUnresolvedSupportRequests(List<SupportRequest> unresolvedSupportRequests) {
        this.unresolvedSupportRequests = unresolvedSupportRequests;
    }

    public SupportRequest getSupportRequestToView() {
        return supportRequestToView;
    }

    public void setSupportRequestToView(SupportRequest supportRequestToView) {
        this.supportRequestToView = supportRequestToView;
    }

    public SupportRequest getSupportRequestToUpdate() {
        return supportRequestToUpdate;
    }

    public void setSupportRequestToUpdate(SupportRequest supportRequestToUpdate) {
        this.supportRequestToUpdate = supportRequestToUpdate;
    }

    public String getNewComment() {
        return newComment;
    }

    public void setNewComment(String newComment) {
        this.newComment = newComment;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Staff getAdmin() {
        return admin;
    }

    public void setAdmin(Staff admin) {
        this.admin = admin;
    }

    public SupportRequest getSupportRequestToToggle() {
        return supportRequestToToggle;
    }

    public void setSupportRequestToToggle(SupportRequest supportRequestToToggle) {
        this.supportRequestToToggle = supportRequestToToggle;
    }
    
}

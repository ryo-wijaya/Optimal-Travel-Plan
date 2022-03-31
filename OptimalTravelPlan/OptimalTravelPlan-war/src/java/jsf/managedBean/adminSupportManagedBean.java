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
import java.util.logging.Level;
import java.util.logging.Logger;
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

    @EJB(name = "BookingSessionBeanLocal")
    private BookingSessionBeanLocal bookingSessionBeanLocal;

    @EJB
    private SupportRequestSessionBeanLocal supportRequestSessionBeanLocal;

    private List<SupportRequest> supportRequests;
    private List<SupportRequest> filteredSupportRequests;
    private List<SupportRequest> unresolvedSupportRequests;

    private SupportRequest supportRequestToView;

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

    public void retrieveBookingByRequest(ActionEvent event) throws SupportRequestNotFoundException {
        this.booking = bookingSessionBeanLocal.retrieveBookingBySupportRequest((Long) event.getComponent().getAttributes().get("serviceId"));
    }

    /**
     * @return the supportRequests
     */
    public List<SupportRequest> getSupportRequests() {
        return supportRequests;
    }

    /**
     * @param supportRequests the supportRequests to set
     */
    public void setSupportRequests(List<SupportRequest> supportRequests) {
        this.supportRequests = supportRequests;
    }

    /**
     * @return the filteredSupportRequests
     */
    public List<SupportRequest> getFilteredSupportRequests() {
        return filteredSupportRequests;
    }

    /**
     * @param filteredSupportRequests the filteredSupportRequests to set
     */
    public void setFilteredSupportRequests(List<SupportRequest> filteredSupportRequests) {
        this.filteredSupportRequests = filteredSupportRequests;
    }

    /**
     * @return the unresolvedSupportRequests
     */
    public List<SupportRequest> getUnresolvedSupportRequests() {
        return unresolvedSupportRequests;
    }

    /**
     * @param unresolvedSupportRequests the unresolvedSupportRequests to set
     */
    public void setUnresolvedSupportRequests(List<SupportRequest> unresolvedSupportRequests) {
        this.unresolvedSupportRequests = unresolvedSupportRequests;
    }

    /**
     * @return the supportRequestToView
     */
    public SupportRequest getSupportRequestToView() {
        return supportRequestToView;
    }

    /**
     * @param supportRequestToView the supportRequestToView to set
     */
    public void setSupportRequestToView(SupportRequest supportRequestToView) {
        this.supportRequestToView = supportRequestToView;
    }

    /**
     * @return the supportRequestToUpdate
     */
    public SupportRequest getSupportRequestToUpdate() {
        return supportRequestToUpdate;
    }

    /**
     * @param supportRequestToUpdate the supportRequestToUpdate to set
     */
    public void setSupportRequestToUpdate(SupportRequest supportRequestToUpdate) {
        this.supportRequestToUpdate = supportRequestToUpdate;
    }

    /**
     * @return the newComment
     */
    public String getNewComment() {
        return newComment;
    }

    /**
     * @param newComment the newComment to set
     */
    public void setNewComment(String newComment) {
        this.newComment = newComment;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}

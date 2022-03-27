/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import ejb.session.stateless.BookingSessionBeanLocal;
import ejb.session.stateless.EmailSessionBeanLocal;
import ejb.session.stateless.ReviewSessionBeanLocal;
import ejb.session.stateless.ServiceSessionBeanLocal;
import entity.Account;
import entity.Booking;
import entity.Business;
import entity.Customer;
import entity.Review;
import entity.Service;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import util.exception.ReviewNotFoundException;

/**
 *
 * @author ryo20
 */
@Named(value = "bookingPageManagedBean")
@ViewScoped
public class BookingPageManagedBean implements Serializable {

    @EJB
    private EmailSessionBeanLocal emailSessionBeanLocal;

    @EJB
    private ServiceSessionBeanLocal serviceSessionBeanLocal;

    @EJB
    private ReviewSessionBeanLocal reviewSessionBeanLocal;

    @EJB
    private BookingSessionBeanLocal bookingSessionBeanLocal;

    private List<Booking> bookings;
    private Booking selectedBooking;
    private List<Review> reviews;
    private Review selectedReview;
    private List<Service> services;
    private Service selectedService;
    private Customer selectedCustomer;
    private String emailMessage;
    private Business business;
    private Service serviceMessage;

    public BookingPageManagedBean() {
    }

    @PostConstruct
    public void PostConstruct() {
        this.business = (Business) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedInAccount");
        bookings = bookingSessionBeanLocal.retrieveBookingsByBusinessId(business.getAccountId());
        reviews = reviewSessionBeanLocal.retrieveReviewsByBusinessId(business.getAccountId());
        // services = serviceSessionBeanLocal.retrieveServicesByBusinessId(business.getAccountId()); 
    }

    public String checkIfReviewHasReply() {
        if (selectedReview.getBusinessReply() == null) {
            return "No";
        } else {
            return "Yes";
        }
    }

    public void reviewReply() {
        try {
            reviewSessionBeanLocal.updateReview(selectedReview);
        } catch (ReviewNotFoundException ex) {
            System.out.println("Error with review reply");
        }
    }

    public void filterReviews() {
        reviews.clear();
        reviews.add(selectedReview);
    }

    public void filterBooking() {
        bookings.clear();
        bookings = bookingSessionBeanLocal.retrieveBookingsByServiceId(selectedService.getServiceId());
    }

    public void sendEmail(ActionEvent event) {
        if (!emailMessage.isEmpty()) {
            String message = "Dear " + selectedCustomer.getName() + ",\n\n"
                    + business.getCompanyName() + " have sent you the following message regarding " + serviceMessage.getServiceName() + ": \n\n"
                    + emailMessage + "\n\nThank you for using our booking services!\n\nOptimal Travel Plan\n\nThis is a system generated message. Please do not reply!";
            try{
            emailSessionBeanLocal.emailCheckoutNotificationSync(message, selectedCustomer.getEmail());
            }catch (Exception e){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "EJB Error! cos no email used haha, caught error", null));
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Message 'sent' to Customer!", null));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Please type in a message to send!", null));
        }
    }

    public void viewCustomerDetails(ActionEvent event) {
        this.selectedCustomer = (Customer) event.getComponent().getAttributes().get("customerToView");
        this.serviceMessage = (Service) event.getComponent().getAttributes().get("serviceForMessage");
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public Booking getSelectedBooking() {
        return selectedBooking;
    }

    public void setSelectedBooking(Booking selectedBooking) {
        this.selectedBooking = selectedBooking;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public Review getSelectedReview() {
        return selectedReview;
    }

    public void setSelectedReview(Review selectedReview) {
        this.selectedReview = selectedReview;
    }

    public String getEmailMessage() {
        return emailMessage;
    }

    public void setEmailMessage(String emailMessage) {
        this.emailMessage = emailMessage;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public Service getSelectedService() {
        return selectedService;
    }

    public void setSelectedService(Service selectedService) {
        this.selectedService = selectedService;
    }

    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }

}

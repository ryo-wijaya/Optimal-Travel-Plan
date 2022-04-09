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
import entity.Booking;
import entity.Business;
import entity.Customer;
import entity.Review;
import entity.Service;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import util.exception.ReviewNotFoundException;

@Named(value = "bookingPageManagedBean")
@ViewScoped
public class BookingPageManagedBean implements Serializable {

    @Resource(name = "optimalTravelPlanDataSource")
    private DataSource optimalTravelPlanDataSource;

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
    private Customer selectedCustomer;
    private String emailMessage;
    private Business business;
    private Service serviceMessage;
    private List<Booking> filteredBookings;
    private Date bookingSearchStartDate;
    private Date bookingSearchEndDate;



    // Somehow selectedBooking.startDate or endDate will be null during an setPropertyActionListener for selectedBooking, so gotta do dis way
    private Date startDate;
    private Date endDate;

    public BookingPageManagedBean() {
        selectedReview = new Review();
        selectedBooking = new Booking();
    }

    @PostConstruct
    public void PostConstruct() {
        this.business = (Business) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedInAccount");
        bookings = bookingSessionBeanLocal.retrieveBookingsByBusinessId(business.getAccountId());
        reviews = reviewSessionBeanLocal.retrieveReviewsByBusinessId(business.getAccountId());
        startDate = new Date();
        endDate = new Date();
    }

    public void generateReport(ActionEvent event) {

        try {
            String description = "initial value";
            HashMap parameters = new HashMap();

            Calendar startingDateToSearch = Calendar.getInstance();
            startingDateToSearch.setTime(bookingSearchStartDate);

            Calendar endingDateToSearch = Calendar.getInstance();
            endingDateToSearch.setTime(bookingSearchEndDate);

            SimpleDateFormat month_date = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
            String startDateToSearch = month_date.format(bookingSearchStartDate);
            String endDateToSearch = month_date.format(bookingSearchEndDate);

            description = business.getCompanyName() + " - Booking from " + startingDateToSearch.get(Calendar.DAY_OF_MONTH) + " " + startDateToSearch + " to "
                    + endingDateToSearch.get(Calendar.DAY_OF_MONTH) + " " + endDateToSearch;
            System.out.println("startingDateToSearch = " + startingDateToSearch);
            System.out.println("startingDateToSearch.get(Calendar.MONTH) = " + startingDateToSearch.get(Calendar.MONTH));
            
            ChronoLocalDateTime startDateFilter = LocalDateTime.of(
                    startingDateToSearch.get(Calendar.YEAR), 
                    Month.of(startingDateToSearch.get(Calendar.MONTH) + 1), 
                    startingDateToSearch.get(Calendar.DAY_OF_MONTH), 
                    startingDateToSearch.get(Calendar.HOUR_OF_DAY), 
                    startingDateToSearch.get(Calendar.MINUTE));
            
            ChronoLocalDateTime endDateFilter = LocalDateTime.of(
                    endingDateToSearch.get(Calendar.YEAR),
                    Month.of(endingDateToSearch.get(Calendar.MONTH) + 1), 
                    endingDateToSearch.get(Calendar.DAY_OF_MONTH), 
                    endingDateToSearch.get(Calendar.HOUR_OF_DAY), 
                    endingDateToSearch.get(Calendar.MINUTE));
            
            parameters.put("date", description);
            parameters.put("description", description);
            parameters.put("startDateFilter", startDateFilter);
            parameters.put("endDateFilter", endDateFilter);
            parameters.put("ServiceID", business.getAccountId());
            parameters.put("BusinessID", business.getAccountId());
            
            InputStream reportStream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(
                    "/jasperHeadache/Booking_BusinessID_date.jasper");
            OutputStream outputStream = FacesContext.getCurrentInstance().getExternalContext().getResponseOutputStream();

            JasperRunManager.runReportToPdfStream(reportStream, outputStream, parameters, optimalTravelPlanDataSource.getConnection());

        } catch (IOException | JRException | SQLException | IllegalStateException ex) {
            System.out.println("Exception thrown : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void reviewReply() {
        try {
            reviewSessionBeanLocal.updateReview(selectedReview);
            this.emailMessage = "The business has added/edited a reply to your review on booking" + selectedReview.getBooking().getBookingId()
                    + "\n\nThe new/edited reply is:\n\n" + selectedReview.getBusinessReply();
            this.selectedCustomer = selectedReview.getBooking().getTravelItinerary().getCustomer();
            //this.sendEmail();
        } catch (ReviewNotFoundException ex) {
            System.out.println("Error with review reply");
        }
    }

    public void updatePage() {
        bookings = bookingSessionBeanLocal.retrieveBookingsByBusinessId(business.getAccountId());
        reviews = reviewSessionBeanLocal.retrieveReviewsByBusinessId(business.getAccountId());
    }

    public void filterReview(ActionEvent event) {
        reviews.clear();
        selectedReview = (Review) event.getComponent().getAttributes().get("selectedFilterReview");
        reviews.add(selectedReview);

    }

    public void filterBooking(ActionEvent event) throws IOException {
        bookings.clear();
        selectedBooking = (Booking) event.getComponent().getAttributes().get("selectedFilterBooking");
        bookings.add(selectedBooking);
    }

    public void sendEmail() {
        if (!emailMessage.isEmpty()) {
            String message = "Dear " + selectedCustomer.getName() + ",\n\n"
                    + business.getCompanyName() + " have sent you the following message regarding " + serviceMessage.getServiceName() + ": \n\n"
                    + emailMessage + "\n\nThank you for using our booking services!\n\nOptimal Travel Plan\n\nThis is a system generated message. Please do not reply!";
            try {
                emailSessionBeanLocal.emailCheckoutNotificationSync(message, selectedCustomer.getEmail());
            } catch (Exception e) {
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

//    public void editBooking() {
//        System.out.println("Start edit method");
//        System.out.println("selected booking" + selectedBooking.toString());
//
//        if (selectedBooking.getStartDate().equals(startDate) && selectedBooking.getEndDate().equals(endDate)) {
//            // No change made!
//            return;
//        }
//
//        // Somehow selectedBooking.startDate or endDate will be null during an setPropertyActionListener for selectedBooking, so gotta do dis way
//        selectedBooking.setStartDate(startDate);
//        selectedBooking.setEndDate(endDate);
//        try {
//            bookingSessionBeanLocal.updateBooking(selectedBooking);
//
//            Customer customer = selectedBooking.getTravelItinerary().getCustomer();
//
//            emailMessage = "The date of your booking has been changed to a start date of: " + startDate.toString() + " and an end date of: " + endDate.toString();
//
//            String message = "Dear " + customer.getName() + ",\n\n"
//                    + business.getCompanyName() + " have sent you the following message regarding " + serviceMessage.getServiceName() + ": \n\n"
//                    + emailMessage + "\n\nThank you for using our booking services!\n\nOptimal Travel Plan\n\nThis is a system generated message. Please do not reply!";
//
//            emailSessionBeanLocal.emailCheckoutNotificationSync(message, customer.getEmail());
//        } catch (BookingNotFoundException | UpdateBookingException ex) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Start date cannot exceed end date!", null));
//            this.updatePage();
//        } catch (Exception ex) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to send email - coz no email address", null));
//        }
//    }


   

    public List<Booking> getFilteredBookings() {
        return filteredBookings;
    }

    public void setFilteredBookings(List<Booking> filteredBookings) {
        this.filteredBookings = filteredBookings;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public Service getServiceMessage() {
        return serviceMessage;
    }

    public void setServiceMessage(Service serviceMessage) {
        this.serviceMessage = serviceMessage;
    }

    public Date getBookingSearchStartDate() {
        return bookingSearchStartDate;
    }

    public void setBookingSearchStartDate(Date bookingSearchStartDate) {
        this.bookingSearchStartDate = bookingSearchStartDate;
    }

    public Date getBookingSearchEndDate() {
        return bookingSearchEndDate;
    }

    public void setBookingSearchEndDate(Date bookingSearchEndDate) {
        this.bookingSearchEndDate = bookingSearchEndDate;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedBean;

import ejb.session.stateless.BookingSessionBeanLocal;
import entity.Booking;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import util.exception.SupportRequestNotFoundException;

@Named(value = "singleBookingManagedBean")
@ViewScoped
public class singleBookingManagedBean implements Serializable {

    @EJB
    private BookingSessionBeanLocal bookingSessionBeanLocal;

    private Booking booking;

    public singleBookingManagedBean() {
    }

    public void retrieveBookingByRequest(ActionEvent event) throws SupportRequestNotFoundException {
        this.booking = bookingSessionBeanLocal.retrieveBookingBySupportRequest((Long) event.getComponent().getAttributes().get("serviceId"));
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

}

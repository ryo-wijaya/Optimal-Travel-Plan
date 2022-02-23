/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Booking;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.BookingNotFoundException;

@Stateless
public class BookingSessionBean implements BookingSessionBeanLocal {

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;

    
    
    
    @Override
    public Booking retrieveBookingById(Long bookingId) throws BookingNotFoundException {
        Booking booking = em.find(Booking.class, bookingId);
        if (booking != null) {
            booking.getTravelItinerary(); //lazy loading
            booking.getPaymentTransaction();
            booking.getSupportRequest();
            booking.getReview();
            booking.getService();
            return booking;
        } else {
            throw new BookingNotFoundException();
        }
    }
}

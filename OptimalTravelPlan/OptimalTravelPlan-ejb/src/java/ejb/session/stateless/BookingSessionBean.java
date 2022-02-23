/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Booking;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.BookingNotFoundException;

@Stateless
public class BookingSessionBean implements BookingSessionBeanLocal {

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;

    //Create method needs to take in Service and TravelItinerary ID, possibly immediately create a transaction
    //Does creating a traveliternerary mean that a booking must be created? What if a customer wants to have an itinerary just for reference?
    
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

    @Override
    public List<Booking> retrieveAllBookings() {
        Query query = em.createQuery("SELECT b FROM Booking b");
        List<Booking> bookings = query.getResultList();
        for (Booking booking : bookings) { //lazy loading
            booking.getTravelItinerary(); 
            booking.getPaymentTransaction();
            booking.getSupportRequest();
            booking.getReview();
            booking.getService();
        }
        return bookings;
    }
}

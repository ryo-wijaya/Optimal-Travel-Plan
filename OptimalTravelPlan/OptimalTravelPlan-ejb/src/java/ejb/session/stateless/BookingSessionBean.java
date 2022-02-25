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
import util.exception.BookingAlreadyConfirmedException;
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
            //remove loading since by default (one to one / many to one) are EARGER fetch
            return booking;
        } else {
            throw new BookingNotFoundException();
        }
    }

    @Override
    public List<Booking> retrieveAllBookings() {
        Query query = em.createQuery("SELECT b FROM Booking b");
        return query.getResultList();
        //remove loading since by default (one to one / many to one) are EARGER fetch
    }

    @Override
    public List<Booking> retrieveBookingsByServiceId(Long serviceId) {
        Query query = em.createQuery("SELECT b FROM Service s JOIN s.bookings b WHERE s.serviceId = :serviceId");
        query.setParameter("serviceId", serviceId);
        return query.getResultList();
    }

    @Override
    public void deleteBookingById(Long bookingId) throws BookingNotFoundException, BookingAlreadyConfirmedException {
        Booking booking = retrieveBookingById(bookingId);
        if (booking.getPaymentTransaction() != null) {
            throw new BookingAlreadyConfirmedException("Booking has been paid and confirmed!");
        }
        booking.getService().removeBooking(booking);
        booking.getTravelItinerary().removeBooking(booking);
        //cascade type for review and support = Cascade.remove
        em.remove(booking);
        em.flush();
    }
}

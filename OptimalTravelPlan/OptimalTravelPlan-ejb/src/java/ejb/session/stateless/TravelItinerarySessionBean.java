/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Booking;
import entity.Customer;
import entity.Service;
import entity.Tag;
import entity.TravelItinerary;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.AccountNotFoundException;
import util.exception.BookingAlreadyConfirmedException;
import util.exception.BookingNotFoundException;
import util.exception.ConstraintViolationException;
import util.exception.TravelItineraryNotFoundException;
import util.exception.UnknownPersistenceException;

@Stateless
public class TravelItinerarySessionBean implements TravelItinerarySessionBeanLocal {

    @EJB(name = "BookingSessionBeanLocal")
    private BookingSessionBeanLocal bookingSessionBeanLocal;

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewTravelItinerary(TravelItinerary travelItinerary, Long customerId) throws UnknownPersistenceException, ConstraintViolationException, AccountNotFoundException {
        try {
            Customer customer = em.find(Customer.class, customerId);
            if (customer == null) {
                throw new AccountNotFoundException("Customer not found!");
            }

            travelItinerary.setCustomer(customer);
            em.persist(travelItinerary);
            customer.getTravelItineraries().add(travelItinerary);
            em.flush();
            return travelItinerary.getTravelItineraryId();
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new ConstraintViolationException();
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }

    @Override
    public TravelItinerary retrieveTravelItineraryById(Long travelItineraryId) throws TravelItineraryNotFoundException {
        TravelItinerary travelItinerary = em.find(TravelItinerary.class, travelItineraryId);
        if (travelItinerary != null) {
            return travelItinerary;
        } else {
            throw new TravelItineraryNotFoundException("Travel Itinerary not found!");
        }
    }

    @Override
    public void deleteTravelItinerary(Long travelItineraryId) throws TravelItineraryNotFoundException, BookingNotFoundException, BookingAlreadyConfirmedException {
        TravelItinerary travelItinerary = this.retrieveTravelItineraryById(travelItineraryId);
        if (travelItinerary == null) {
            throw new TravelItineraryNotFoundException("Travel Itinerary not found!");
        }
        for (Booking booking : travelItinerary.getBookings()) {
            bookingSessionBeanLocal.deleteBookingById(booking.getBookingId());
        }
    }

    @Override
    public TravelItinerary recommendTravelItinerary(TravelItinerary travelItinerary) {
        travelItinerary = em.find(TravelItinerary.class, travelItinerary.getTravelItineraryId());
        List<Tag> tags = travelItinerary.getCustomer().getFavouriteTags();
        Date startDate = travelItinerary.getStartDate();
        Date endDate = travelItinerary.getEndDate();
        List<Booking> currBooking = travelItinerary.getBookings();


        Query query = em.createQuery("SELECT s,COUNT(s) FROM Tag t JOIN t.services s WHERE t IN :tags GROUP BY s ORDER BY COUNT(s)"); // OR
        query.setParameter("tags", tags);
        List<Object[]> result = query.getResultList();
        
        System.out.print();
        
        
        throw new UnsupportedOperationException();
    }
}

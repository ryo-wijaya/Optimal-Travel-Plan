/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.TravelItinerary;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.Local;
import util.exception.AccountNotFoundException;
import util.exception.BookingAlreadyConfirmedException;
import util.exception.BookingNotFoundException;
import util.exception.ConstraintViolationException;
import util.exception.CreateNewBookingException;
import util.exception.PaymentAccountNotFoundException;
import util.exception.TravelItineraryNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateTravelItineraryException;

/**
 *
 * @author sucram
 */
@Local
public interface TravelItinerarySessionBeanLocal {

    public Long createNewTravelItinerary(TravelItinerary travelItinerary, Long customerId, Long countryId) throws UnknownPersistenceException, ConstraintViolationException, AccountNotFoundException;
    
    public TravelItinerary retrieveTravelItineraryById(Long travelItineraryId) throws TravelItineraryNotFoundException;

    public void deleteTravelItinerary(Long travelItineraryId) throws TravelItineraryNotFoundException, BookingNotFoundException, BookingAlreadyConfirmedException;

    public TravelItinerary recommendTravelItinerary(TravelItinerary travelItinerary) throws ConstraintViolationException, UnknownPersistenceException, CreateNewBookingException ;

    public BigDecimal calculateTotalItineraryPrice(TravelItinerary travelItinerary);

    public TravelItinerary updateTravelItinerary(TravelItinerary travelItinerary) throws TravelItineraryNotFoundException, UpdateTravelItineraryException;

    public List<TravelItinerary> retrieveAllCustomerTravelItinerary(Long customerId) throws AccountNotFoundException;

    public List<TravelItinerary> retrieveAllTravelItineraries();

    public TravelItinerary payForAllBookings(Long travelItineraryId, Long PaymentAccountId)throws TravelItineraryNotFoundException, PaymentAccountNotFoundException, UnknownPersistenceException ;
}

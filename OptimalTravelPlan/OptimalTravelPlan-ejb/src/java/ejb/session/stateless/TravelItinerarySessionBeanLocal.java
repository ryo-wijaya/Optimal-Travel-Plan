/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.TravelItinerary;
import java.math.BigDecimal;
import javax.ejb.Local;
import util.exception.AccountNotFoundException;
import util.exception.BookingAlreadyConfirmedException;
import util.exception.BookingNotFoundException;
import util.exception.ConstraintViolationException;
import util.exception.CreateNewBookingException;
import util.exception.TravelItineraryNotFoundException;
import util.exception.UnknownPersistenceException;

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


}

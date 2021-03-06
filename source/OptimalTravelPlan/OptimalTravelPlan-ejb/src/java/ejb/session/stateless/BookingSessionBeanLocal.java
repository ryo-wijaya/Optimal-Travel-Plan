/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Booking;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.BookingAlreadyConfirmedException;
import util.exception.BookingNotFoundException;
import util.exception.ConstraintViolationException;
import util.exception.CreateNewBookingException;
import util.exception.SupportRequestNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateBookingException;

/**
 *
 * @author sucram
 */
@Local
public interface BookingSessionBeanLocal {

    public Booking retrieveBookingById(Long bookingId) throws BookingNotFoundException;

    public List<Booking> retrieveAllBookings();

    public List<Booking> retrieveBookingsByServiceId(Long serviceId);

    public void deleteBookingById(Long bookingId) throws BookingNotFoundException, BookingAlreadyConfirmedException;

    public Long createBooking(Booking newBooking, Long serviceId, Long travelItineraryId) throws ConstraintViolationException, UnknownPersistenceException, CreateNewBookingException;

    public List<Booking> retrieveBookingsByBusinessId(Long businessId);

    public void updateBooking(Booking booking) throws BookingNotFoundException, UpdateBookingException;

    public Booking retrieveBookingBySupportRequest(Long supportRequestId) throws SupportRequestNotFoundException;
    
    public BigDecimal getPricingOfBooking(Long bookingId, Date startDate, Date endDate) throws BookingNotFoundException;
}

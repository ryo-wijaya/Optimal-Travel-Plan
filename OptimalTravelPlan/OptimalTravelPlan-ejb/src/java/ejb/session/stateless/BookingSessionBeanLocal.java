/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Booking;
import java.util.List;
import javax.ejb.Local;
import util.exception.BookingAlreadyConfirmedException;
import util.exception.BookingNotFoundException;

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
    
}

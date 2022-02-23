/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Booking;
import entity.SupportRequest;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import util.exception.BookingNotFoundException;
import util.exception.ConstraintViolationException;
import util.exception.CreateSupportRequestException;
import util.exception.UnknownPersistenceException;

@Stateless
public class SupportRequestSessionBean implements SupportRequestSessionBeanLocal {

    @EJB
    private BookingSessionBeanLocal bookingSessionBeanLocal;

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;

    public Long createNewSupportRequest(SupportRequest newSupportRequest, Long bookingId) throws UnknownPersistenceException, ConstraintViolationException,
            CreateSupportRequestException {
        try {

            Booking booking = bookingSessionBeanLocal.retrieveBookingById(bookingId);
            if (booking.getSupportRequest() != null) {
                // check if a booking already has a supportRequest
                throw new CreateSupportRequestException();
            }

            em.persist(newSupportRequest);
            newSupportRequest.setBooking(booking);
            booking.setSupportRequest(newSupportRequest);
            em.flush();
            return newSupportRequest.getSupportRequestId();

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
        } catch (BookingNotFoundException ex) {
            throw new CreateSupportRequestException("Booking is already tied to a SupportRequest!");
        }
    }

    public SupportRequest retrieveSupportRequestById(Long supportRequestId) {
        return null;
    }
}

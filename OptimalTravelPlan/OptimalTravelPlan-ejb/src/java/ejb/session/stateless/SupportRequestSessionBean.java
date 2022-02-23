/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Booking;
import entity.SupportRequest;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.BookingNotFoundException;
import util.exception.ConstraintViolationException;
import util.exception.CreateSupportRequestException;
import util.exception.ResolveSupportRequestException;
import util.exception.SupportRequestNotFoundException;
import util.exception.UnknownPersistenceException;

@Stateless
public class SupportRequestSessionBean implements SupportRequestSessionBeanLocal {

    @EJB
    private BookingSessionBeanLocal bookingSessionBeanLocal;

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;

    @Override
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

    @Override
    public SupportRequest retrieveSupportRequestById(Long supportRequestId) throws SupportRequestNotFoundException {
        SupportRequest supportRequest = em.find(SupportRequest.class, supportRequestId);
        if (supportRequest != null) {
            supportRequest.getBooking();
            return supportRequest;
        } else {
            throw new SupportRequestNotFoundException();
        }
    }
    
    @Override
    public List<SupportRequest> retrieveAllSupportRequests() {
        Query query = em.createQuery("SELECT sr FROM SupportRequest sr");
        List<SupportRequest> supportRequests = query.getResultList();
        for (SupportRequest sr : supportRequests) { //lazy loading
            sr.getBooking();
        }
        return supportRequests;
    }
    
    @Override
    public List<SupportRequest> retrieveAllUnresolvedSupportRequests() {
        Query query = em.createQuery("SELECT sr FROM SupportRequest sr WHERE sr.resolved = false");
        List<SupportRequest> supportRequests = query.getResultList();
        for (SupportRequest sr : supportRequests) { //lazy loading
            sr.getBooking();
        }
        return supportRequests;
    }
    
    @Override
    public void resolveSupportRequest(Long supportRequestId) throws SupportRequestNotFoundException, ResolveSupportRequestException {
        SupportRequest supportRequest = this.retrieveSupportRequestById(supportRequestId);
        if (!supportRequest.getResolved()) {
            supportRequest.setResolved(Boolean.TRUE);
        } else {
            throw new ResolveSupportRequestException("Support request is already resolved!");
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Booking;
import entity.Customer;
import entity.Service;
import entity.ServiceRate;
import entity.Tag;
import entity.TravelItinerary;
import java.util.ArrayList;
import java.util.Calendar;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.enumeration.ServiceType;
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

    //Country
    @Override
    public TravelItinerary recommendTravelItinerary(TravelItinerary travelItinerary) {
        travelItinerary = em.find(TravelItinerary.class, travelItinerary.getTravelItineraryId());
        List<Tag> tags = travelItinerary.getCustomer().getFavouriteTags();
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(travelItinerary.getStartDate());
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.HOUR_OF_DAY, -3);
        endDate.setTime(travelItinerary.getEndDate());
        List<Booking> currBooking = new ArrayList<Booking>(travelItinerary.getBookings());

        Query query = em.createQuery("SELECT s,COUNT(s) FROM Tag t JOIN t.services s WHERE t IN :tags AND s.serviceType == :entertainment GROUP BY s ORDER BY COUNT(s)"); // OR
        query.setParameter("tags", tags);
        query.setParameter("entertainment", ServiceType.ENTERTAINMENT);
        List<Object[]> result = query.getResultList();
        List<Service> services = new ArrayList<>();
        for (Object[] arr : result) {
            System.out.print("arr[0] = " + arr[0] + " arr[1] = " + arr[1]);
            services.add((Service) arr[0]);
        }

        Query query2 = em.createQuery("SELECT s FROM Service s WHERE s.serviceType == :hotel");
        query2.setParameter("hotel", ServiceType.HOTEL);
        List<Service> hotels = query2.getResultList();

        Query query3 = em.createQuery("SELECT s FROM Service s WHERE s.serviceType == :FnB");
        query3.setParameter("FnB", ServiceType.FOOD_AND_BEVERAGE);
        List<Service> FnB = query3.getResultList();

        moveToDaylight(startDate);

        Calendar foodPointer = (Calendar) startDate.clone();
        while (foodPointer.before(endDate)) {
            addMeals(travelItinerary, foodPointer, FnB);
            foodPointer.add(Calendar.DAY_OF_MONTH, 1);
            moveToDaylight(foodPointer);
        }

        Calendar hotelPointer = (Calendar) startDate.clone();
        while (hotelPointer.before(endDate)) {
            addHotels(travelItinerary, hotelPointer, hotels);
            hotelPointer.add(Calendar.DAY_OF_MONTH, 1);
            moveToDaylight(hotelPointer);
        }

        Calendar entertainmentPointer = (Calendar) startDate.clone();
        while (entertainmentPointer.before(endDate)) {
            addEntertainment(travelItinerary, entertainmentPointer, services);
            entertainmentPointer.add(Calendar.DAY_OF_MONTH, 1);
            moveToDaylight(entertainmentPointer);
        }

        return travelItinerary;
    }

    private void addEntertainment(TravelItinerary travelItinerary, Calendar entertainmentPointer, List<Service> services) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    private void addHotels(TravelItinerary ti, Calendar day, List<Service> hotels) {
        throw new UnsupportedOperationException();
    }

    private void addMeals(TravelItinerary ti, Calendar day, List<Service> meals) {
        List<Booking> sameDay = new ArrayList<>();
        sameDay.sort((Booking a, Booking b) -> {
            return a.getStartDate().compareTo(b.getStartDate());
        });
        for (Booking booking : ti.getBookings()) {
            Calendar formatter = Calendar.getInstance();
            formatter.setTime(booking.getStartDate());
            if (formatter.get(Calendar.YEAR) == day.get(Calendar.YEAR) && formatter.get(Calendar.DAY_OF_YEAR) == day.get(Calendar.DAY_OF_YEAR)) {
                sameDay.add(booking);
            }
        }
        Boolean hadLunch = false;
        for (Booking booking : sameDay) {
            Calendar formatter = Calendar.getInstance();
            formatter.setTime(booking.getStartDate());
            Calendar start = (Calendar) formatter.clone();
            start.set(Calendar.HOUR_OF_DAY, 11);
            Calendar formatter2 = Calendar.getInstance();
            formatter2.setTime(booking.getEndDate());
            Calendar end = (Calendar) formatter.clone();
            end.set(Calendar.HOUR_OF_DAY, 11);
            if (formatter.before(end) && formatter2.after(start)) {
                hadLunch = true;
            }
        }
        if (hadLunch = false) {
            for (int i = 0; i < sameDay.size(); i++) {
                Calendar formatter = Calendar.getInstance();
                formatter.setTime(sameDay.get(i).getEndDate());
                Calendar start = (Calendar) formatter.clone();
                start.set(Calendar.HOUR_OF_DAY, 9);
                Calendar end = (Calendar) start.clone();
                end.set(Calendar.HOUR_OF_DAY, 15);
                if (formatter.after(start) && sameDay.get(i + 1).getStartDate().getTime() - formatter.getTime().getTime() > 3600000l) {
                    Service lunch = meals.remove(0);
                    Date startDate = (Date) sameDay.get(i).getEndDate().clone();
                    Date endDate = (Date) startDate.clone();
                    endDate.setTime(endDate.getTime() + 3600000l);
                    Booking newBooking = new Booking(startDate, endDate, ti, lunch);
                    ti.addBooking(newBooking);
                    bookingSessionBeanLocal.createNewBooking(newBooking);
                    hadLunch = true;
                    break;
                } else if (formatter.after(end)) {
                    break;
                }
            }
        }

        Boolean hadDinner = false;
        for (Booking booking : sameDay) {
            Calendar formatter = Calendar.getInstance();
            formatter.setTime(booking.getStartDate());
            Calendar start = (Calendar) formatter.clone();
            start.set(Calendar.HOUR_OF_DAY, 17);
            Calendar formatter2 = Calendar.getInstance();
            formatter2.setTime(booking.getEndDate());
            Calendar end = (Calendar) formatter.clone();
            end.set(Calendar.HOUR_OF_DAY, 22);
            if (formatter.before(end) && formatter2.after(start)) {
                hadDinner = true;
            }
        }
        if (hadDinner = false) {
            for (int i = 0; i < sameDay.size(); i++) {
                Calendar formatter = Calendar.getInstance();
                formatter.setTime(sameDay.get(i).getEndDate());
                Calendar start = (Calendar) formatter.clone();
                start.set(Calendar.HOUR_OF_DAY, 17);
                Calendar end = (Calendar) start.clone();
                end.set(Calendar.HOUR_OF_DAY, 22);
                if (formatter.after(start) && sameDay.get(i + 1).getStartDate().getTime() - formatter.getTime().getTime() > 3600000l) {
                    Service dinner = meals.remove(0);
                    Date startDate = (Date) sameDay.get(i).getEndDate().clone();
                    Date endDate = (Date) startDate.clone();
                    endDate.setTime(endDate.getTime() + 3600000l);
                    Booking newBooking = new Booking(startDate, endDate, ti, dinner);
                    ti.addBooking(newBooking);
                    bookingSessionBeanLocal.createNewBooking(newBooking);
                    hadDinner = true;
                    break;
                } else if (formatter.after(end)) {
                    break;
                }
            }
        }
    }

    private void cleanTime(Calendar formatter) {
        formatter.set(Calendar.HOUR_OF_DAY, 0);
        formatter.set(Calendar.MINUTE, 0);
        formatter.set(Calendar.SECOND, 0);
        formatter.set(Calendar.MILLISECOND, 0);
    }

    private void moveToDaylight(Calendar formatter) {
        Calendar morning = (Calendar) formatter.clone();
        cleanTime(morning);
        morning.set(Calendar.HOUR_OF_DAY, 9);
        Calendar night = (Calendar) formatter.clone();
        cleanTime(night);
        night.set(Calendar.HOUR_OF_DAY, 21);
        if (formatter.before(morning)) {
            formatter.set(Calendar.HOUR_OF_DAY, 9);
        } else if (formatter.after(night)) {
            formatter.add(Calendar.DAY_OF_MONTH, 1);
            formatter.set(Calendar.HOUR_OF_DAY, 9);
        }
    }

}
} 

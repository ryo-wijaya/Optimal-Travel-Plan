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
import util.exception.CreateNewBookingException;
import util.exception.TravelItineraryNotFoundException;
import util.exception.UnknownPersistenceException;

@Stateless
public class TravelItinerarySessionBean implements TravelItinerarySessionBeanLocal {

    @EJB(name = "ServiceSessionBeanLocal")
    private ServiceSessionBeanLocal serviceSessionBeanLocal;

    @EJB(name = "BookingSessionBeanLocal")
    private BookingSessionBeanLocal bookingSessionBeanLocal;

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;

    private static final Integer LUNCH_HOUR_STARTING_TIME = 10;
    private static final Integer LUNCH_HOUR_ENDING_TIME = 15;
    private static final Integer DINNER_HOUR_STARTING_TIME = 17;
    private static final Integer DINNER_HOUR_ENDING_TIME = 21;
    private static final Long HOUR_IN_MILLISECONDS = 3600000L;
    private static final Long HALF_HOUR_IN_MILLISECONDS = 1800000L;

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

    //Country not added. Ensure there are always one hotel/F&B/Entertainment in each country else prepare for errors!
    @Override
    public TravelItinerary recommendTravelItinerary(TravelItinerary travelItinerary) throws ConstraintViolationException, UnknownPersistenceException, CreateNewBookingException {
        travelItinerary = em.find(TravelItinerary.class, travelItinerary.getTravelItineraryId());
        List<Tag> tags = travelItinerary.getCustomer().getFavouriteTags();
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(travelItinerary.getStartDate());
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.HOUR_OF_DAY, -3);
        endDate.setTime(travelItinerary.getEndDate());
        List<Booking> currBooking = new ArrayList<Booking>(travelItinerary.getBookings());

        Query query = em.createQuery("SELECT s,COUNT(s) FROM Tag t JOIN t.services s WHERE t IN :tags AND s.serviceType == :entertainment GROUP BY s ORDER BY COUNT(s) ASC"); // OR
        query.setParameter("tags", tags);
        query.setParameter("entertainment", ServiceType.ENTERTAINMENT);
        List<Object[]> result = query.getResultList();
        List<Service> services = new ArrayList<>();
        
        for (Object[] arr : result) {
            System.out.print("arr[0] = " + arr[0] + " arr[1] = " + arr[1]);
            services.add((Service) arr[0]);
        }

        if (services.size() < 5) {
            services = serviceSessionBeanLocal.retrieveAllActiveServices();
        }

        Query query2 = em.createQuery("SELECT s FROM Service s WHERE s.serviceType == :hotel");
        query2.setParameter("hotel", ServiceType.HOTEL);
        List<Service> hotels = query2.getResultList();

        Query query3 = em.createQuery("SELECT s FROM Service s WHERE s.serviceType == :FnB ORDER BY s.rating DESC");
        query3.setParameter("FnB", ServiceType.FOOD_AND_BEVERAGE);
        List<Service> FnB = query3.getResultList();

        moveToDaylight(startDate);

        Calendar foodPointer = (Calendar) startDate.clone();
        while (foodPointer.before(endDate)) {
            addMeals(travelItinerary, foodPointer, FnB);
            foodPointer.add(Calendar.DAY_OF_MONTH, 1);
        }

        Calendar hotelPointer = (Calendar) startDate.clone();
        while (hotelPointer.before(endDate)) {
            addHotels(travelItinerary, hotelPointer, hotels);
            hotelPointer.add(Calendar.DAY_OF_MONTH, 1);
        }

        Calendar entertainmentPointer = (Calendar) startDate.clone();
        while (entertainmentPointer.before(endDate)) {
            addEntertainment(travelItinerary, entertainmentPointer, services);
            entertainmentPointer.add(Calendar.DAY_OF_MONTH, 1);
            moveToDaylight(entertainmentPointer);
        }

        return travelItinerary;
    }

    private void addEntertainment(TravelItinerary travelItinerary, Calendar entertainmentPointer, List<Service> services) throws ConstraintViolationException, UnknownPersistenceException, CreateNewBookingException {
        List<Booking> sameDay = new ArrayList<>();
        for (Booking booking : travelItinerary.getBookings()) {
            Calendar formatter = Calendar.getInstance();
            formatter.setTime(booking.getStartDate());
            if (formatter.get(Calendar.YEAR) == entertainmentPointer.get(Calendar.YEAR)
                    && formatter.get(Calendar.DAY_OF_YEAR) == entertainmentPointer.get(Calendar.DAY_OF_YEAR)
                    && booking.getService().getServiceType() != ServiceType.HOTEL) {
                sameDay.add(booking);
            }
        }

        sameDay.sort((Booking a, Booking b) -> {
            return a.getStartDate().compareTo(b.getStartDate());
        });

        while (entertainmentPointer.get(Calendar.HOUR_OF_DAY) <= 20) {
            if (sameDay.size() < 1 || entertainmentPointer.getTimeInMillis() + 3 * HOUR_IN_MILLISECONDS <= sameDay.get(0).getStartDate().getTime()) {
                Service entertainment = services.remove(0);
                services.add(entertainment);
                Calendar startDate = (Calendar) entertainmentPointer.clone();
                Calendar endDate = (Calendar) entertainmentPointer.clone();
                endDate.add(Calendar.HOUR_OF_DAY, 2);
                Booking newBooking = new Booking(startDate.getTime(), endDate.getTime(), travelItinerary, entertainment);
                bookingSessionBeanLocal.createBooking(newBooking, entertainment.getServiceId(), travelItinerary.getTravelItineraryId());
                entertainmentPointer.add(Calendar.HOUR_OF_DAY, 3);
            } else {
                Calendar midnight =  (Calendar) entertainmentPointer.clone();
                midnight.set(Calendar.HOUR_OF_DAY, 22);
                if (!sameDay.get(0).getEndDate().after(midnight.getTime())) {
                    entertainmentPointer.setTimeInMillis(sameDay.remove(0).getEndDate().getTime() + HOUR_IN_MILLISECONDS);
                } else {
                    break;
                }
            }
        }
    }

    private void addHotels(TravelItinerary ti, Calendar day, List<Service> hotels) throws ConstraintViolationException, UnknownPersistenceException, CreateNewBookingException {
        for (Booking booking : ti.getBookings()) {
            Calendar formatter = Calendar.getInstance();
            formatter.setTime(booking.getStartDate());
            if (formatter.get(Calendar.YEAR) == day.get(Calendar.YEAR)
                    && formatter.get(Calendar.DAY_OF_YEAR) == day.get(Calendar.DAY_OF_YEAR)
                    && booking.getService().getServiceType() == ServiceType.HOTEL) {
                return;
            }
        }
        Calendar startDate = (Calendar) day.clone();
        Calendar endDate = (Calendar) day.clone();
        startDate.set(Calendar.HOUR_OF_DAY, 15);
        endDate.set(Calendar.HOUR_OF_DAY, 12);
        endDate.add(Calendar.DAY_OF_MONTH, 1);
        Service hotel = hotels.remove(0);
        hotels.add(hotel);
        Booking newBooking = new Booking(startDate.getTime(), endDate.getTime(), ti, hotel);
        bookingSessionBeanLocal.createBooking(newBooking, hotel.getServiceId(), ti.getTravelItineraryId());
    }

    private void addMeals(TravelItinerary ti, Calendar day, List<Service> meals) throws ConstraintViolationException, UnknownPersistenceException, CreateNewBookingException {
        List<Booking> sameDay = new ArrayList<>();

        for (Booking booking : ti.getBookings()) {
            Calendar formatter = Calendar.getInstance();
            formatter.setTime(booking.getStartDate());
            if (formatter.get(Calendar.YEAR) == day.get(Calendar.YEAR)
                    && formatter.get(Calendar.DAY_OF_YEAR) == day.get(Calendar.DAY_OF_YEAR)
                    && booking.getService().getServiceType() != ServiceType.HOTEL) {
                sameDay.add(booking);
            }
        }
        sameDay.sort((Booking a, Booking b) -> {
            return a.getStartDate().compareTo(b.getStartDate());
        });
        Boolean hadLunch = false;
        Boolean hadDinner = false;
        Boolean lunchOverlap = false;
        Boolean dinnerOverlap = false;

        Calendar lunchStart = (Calendar) day.clone();
        Calendar dinnerStart = (Calendar) day.clone();
        lunchStart.set(Calendar.HOUR_OF_DAY, LUNCH_HOUR_STARTING_TIME);
        dinnerStart.set(Calendar.HOUR_OF_DAY, DINNER_HOUR_STARTING_TIME);

        Calendar lunchEnd = (Calendar) day.clone();
        Calendar dinnerEnd = (Calendar) day.clone();
        lunchEnd.set(Calendar.HOUR_OF_DAY, LUNCH_HOUR_ENDING_TIME);
        dinnerEnd.set(Calendar.HOUR_OF_DAY, DINNER_HOUR_ENDING_TIME);

        for (Booking booking : sameDay) {

            Calendar formatter = Calendar.getInstance();
            formatter.setTime(booking.getStartDate());
            Calendar formatter2 = Calendar.getInstance();
            formatter2.setTime(booking.getEndDate());
            if (formatter.before(lunchEnd) || formatter2.after(lunchStart)) {
                lunchOverlap = true;
                if (booking.getService().getServiceType() == ServiceType.FOOD_AND_BEVERAGE) {
                    hadLunch = true;
                }
            } else if (formatter.before(dinnerEnd) || formatter2.after(dinnerStart)) {
                dinnerOverlap = true;
                if (booking.getService().getServiceType() == ServiceType.FOOD_AND_BEVERAGE) {
                    hadDinner = true;
                }
            }
        }
        if (hadLunch = false) {
            Calendar noon = Calendar.getInstance();
            noon.setTime(day.getTime());
            noon.set(Calendar.HOUR_OF_DAY, 12);
            if (lunchOverlap == false) {
                Service lunch = meals.remove(0);
                meals.add(lunch);
                Calendar lunchS = Calendar.getInstance();
                lunchS.setTime(day.getTime());
                lunchS.set(Calendar.HOUR_OF_DAY, 12);
                Calendar lunchE = (Calendar) lunchS.clone();
                lunchE.add(Calendar.HOUR_OF_DAY, 1);
                Booking newBooking = new Booking(lunchS.getTime(), lunchE.getTime(), ti, lunch);
                bookingSessionBeanLocal.createBooking(newBooking, lunch.getServiceId(), ti.getTravelItineraryId());
                hadLunch = true;
            } else {

                for (int i = 0; i < sameDay.size(); i++) {

                    Calendar formatter = Calendar.getInstance();
                    formatter.setTime(sameDay.get(i).getStartDate());
                    Calendar formatter2 = Calendar.getInstance();
                    formatter2.setTime(sameDay.get(i).getEndDate());
                    if (i == 0 && lunchStart.getTimeInMillis() + HOUR_IN_MILLISECONDS <= formatter.getTimeInMillis()) {

                        Service lunch = meals.remove(0);
                        meals.add(lunch);
                        Date endDate = (Date) sameDay.get(i).getStartDate().clone();

                        while (endDate.getTime() - HALF_HOUR_IN_MILLISECONDS >= lunchStart.getTimeInMillis() + HOUR_IN_MILLISECONDS
                                && endDate.getTime() == sameDay.get(i).getStartDate().getTime()
                                || endDate.getTime() - HALF_HOUR_IN_MILLISECONDS >= noon.getTimeInMillis()) {
                            endDate.setTime(endDate.getTime() - HALF_HOUR_IN_MILLISECONDS);
                        }

                        Date startDate = (Date) endDate.clone();
                        startDate.setTime(startDate.getTime() - HOUR_IN_MILLISECONDS);
                        Booking newBooking = new Booking(startDate, endDate, ti, lunch);
                        bookingSessionBeanLocal.createBooking(newBooking, lunch.getServiceId(), ti.getTravelItineraryId());
                        hadLunch = true;
                        break;

                    } else if (formatter2.after(lunchStart)
                            && formatter2.before(lunchEnd)
                            && (i == sameDay.size() - 1 || sameDay.get(i + 1).getStartDate().getTime() - formatter2.getTimeInMillis() >= HOUR_IN_MILLISECONDS)) {
                        Service lunch = meals.remove(0);
                        meals.add(lunch);
                        Date startDate = (Date) sameDay.get(i).getEndDate().clone();

                        if (i == sameDay.size() - 1 || startDate.getTime() + HOUR_IN_MILLISECONDS * 2 <= sameDay.get(i + 1).getStartDate().getTime()) {
                            startDate.setTime(startDate.getTime() + HALF_HOUR_IN_MILLISECONDS);
                        }

                        Date endDate = (Date) startDate.clone();
                        endDate.setTime(endDate.getTime() + HOUR_IN_MILLISECONDS);
                        Booking newBooking = new Booking(startDate, endDate, ti, lunch);
                        bookingSessionBeanLocal.createBooking(newBooking, lunch.getServiceId(), ti.getTravelItineraryId());
                        hadLunch = true;
                        break;
                    } else if (formatter.after(lunchStart)
                            && formatter.before(lunchEnd)
                            && (formatter.getTimeInMillis() - sameDay.get(i - 1).getEndDate().getTime() >= HOUR_IN_MILLISECONDS)) {
                        Service lunch = meals.remove(0);
                        meals.add(lunch);
                        Date endDate = (Date) sameDay.get(i).getStartDate().clone();

                        if (endDate.getTime() - HOUR_IN_MILLISECONDS * 2 >= sameDay.get(i - 1).getEndDate().getTime()) {
                            endDate.setTime(endDate.getTime() - HALF_HOUR_IN_MILLISECONDS);
                        }

                        Date startDate = (Date) endDate.clone();
                        startDate.setTime(startDate.getTime() - HOUR_IN_MILLISECONDS);
                        Booking newBooking = new Booking(startDate, endDate, ti, lunch);
                        bookingSessionBeanLocal.createBooking(newBooking, lunch.getServiceId(), ti.getTravelItineraryId());
                        hadDinner = true;
                        break;
                    }
                }
            }
        }
        if (hadDinner = false) {

            if (dinnerOverlap == false) {
                Service dinner = meals.remove(0);
                meals.add(dinner);
                Calendar dinnerS = Calendar.getInstance();
                dinnerS.setTime(day.getTime());
                dinnerS.set(Calendar.HOUR_OF_DAY, 17);
                Calendar dinnerE = (Calendar) dinnerS.clone();
                dinnerE.add(Calendar.HOUR_OF_DAY, 1);
                Booking newBooking = new Booking(dinnerS.getTime(), dinnerE.getTime(), ti, dinner);
                bookingSessionBeanLocal.createBooking(newBooking, dinner.getServiceId(), ti.getTravelItineraryId());
                hadDinner = true;
            } else {
                for (int i = 0; i < sameDay.size(); i++) {

                    Calendar formatter = Calendar.getInstance();
                    formatter.setTime(sameDay.get(i).getStartDate());
                    Calendar formatter2 = Calendar.getInstance();
                    formatter2.setTime(sameDay.get(i).getEndDate());
                    if (i == 0 && dinnerStart.getTimeInMillis() + HOUR_IN_MILLISECONDS <= formatter.getTimeInMillis()) {

                        Service dinner = meals.remove(0);
                        meals.add(dinner);
                        Date endDate = (Date) sameDay.get(i).getStartDate().clone();

                        if (endDate.getTime() - HALF_HOUR_IN_MILLISECONDS >= dinnerStart.getTimeInMillis() + HOUR_IN_MILLISECONDS) {
                            endDate.setTime(endDate.getTime() - HALF_HOUR_IN_MILLISECONDS);
                        }

                        Date startDate = (Date) endDate.clone();
                        startDate.setTime(startDate.getTime() - HOUR_IN_MILLISECONDS);
                        Booking newBooking = new Booking(startDate, endDate, ti, dinner);
                        bookingSessionBeanLocal.createBooking(newBooking, dinner.getServiceId(), ti.getTravelItineraryId());
                        hadDinner = true;
                        break;

                    } else if (formatter2.after(dinnerStart)
                            && formatter2.before(dinnerEnd)
                            && (i == sameDay.size() - 1 || sameDay.get(i + 1).getStartDate().getTime() - formatter2.getTimeInMillis() >= HOUR_IN_MILLISECONDS)) {
                        Service dinner = meals.remove(0);
                        meals.add(dinner);
                        Date startDate = (Date) sameDay.get(i).getEndDate().clone();

                        if (i == sameDay.size() - 1 || startDate.getTime() + HOUR_IN_MILLISECONDS * 2 <= sameDay.get(i + 1).getStartDate().getTime()) {
                            startDate.setTime(startDate.getTime() + HALF_HOUR_IN_MILLISECONDS);
                        }

                        Date endDate = (Date) startDate.clone();
                        endDate.setTime(endDate.getTime() + HOUR_IN_MILLISECONDS);
                        Booking newBooking = new Booking(startDate, endDate, ti, dinner);
                        bookingSessionBeanLocal.createBooking(newBooking, dinner.getServiceId(), ti.getTravelItineraryId());
                        hadDinner = true;
                        break;
                    } else if (formatter.after(dinnerStart)
                            && formatter.before(dinnerEnd)
                            && (formatter.getTimeInMillis() - sameDay.get(i - 1).getEndDate().getTime() >= HOUR_IN_MILLISECONDS)) {
                        Service dinner = meals.remove(0);
                        meals.add(dinner);
                        Date endDate = (Date) sameDay.get(i).getStartDate().clone();

                        if (endDate.getTime() - HOUR_IN_MILLISECONDS * 2 >= sameDay.get(i - 1).getEndDate().getTime()) {
                            endDate.setTime(endDate.getTime() - HALF_HOUR_IN_MILLISECONDS);
                        }

                        Date startDate = (Date) endDate.clone();
                        startDate.setTime(startDate.getTime() - HOUR_IN_MILLISECONDS);
                        Booking newBooking = new Booking(startDate, endDate, ti, dinner);
                        bookingSessionBeanLocal.createBooking(newBooking, dinner.getServiceId(), ti.getTravelItineraryId());
                        hadDinner = true;
                        break;
                    }
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

    @Override
    public BigDecimal calculateTotalItineraryPrice(TravelItinerary travelItinerary) {
        BigDecimal totalPrice = new BigDecimal(0);
        if (travelItinerary.getBookings() != null) {
            for (Booking booking : travelItinerary.getBookings()) {
                Service service = booking.getService();
                ServiceRate lowestRate = service.getRates().get(0);
                for (ServiceRate currentRate : service.getRates()) {
                    if (currentRate.getStartDate().compareTo(booking.getStartDate()) >= 0 && currentRate.getEndDate().compareTo(booking.getEndDate()) <= 0 && currentRate.compareTo(lowestRate) <= 0) {
                        lowestRate = currentRate;
                    }
                }
                totalPrice = totalPrice.add(lowestRate.getPrice());
            }
        }
        return totalPrice;
    }
}

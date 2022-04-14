/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Booking;
import entity.Country;
import entity.Customer;
import entity.PaymentAccount;
import entity.PaymentTransaction;
import entity.Service;
import entity.ServiceRate;
import entity.Tag;
import entity.TravelItinerary;
import java.util.ArrayList;
import java.util.Calendar;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
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
import util.exception.UpdateTravelItineraryException;

@Stateless
public class TravelItinerarySessionBean implements TravelItinerarySessionBeanLocal {

    @EJB(name = "PaymentAccountSessionBeanLocal")
    private PaymentAccountSessionBeanLocal paymentAccountSessionBeanLocal;

    @EJB(name = "CustomerSessionBeanLocal")
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    @EJB(name = "TagSessionBeanLocal")
    private TagSessionBeanLocal tagSessionBeanLocal;

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
    public Long createNewTravelItinerary(TravelItinerary travelItinerary, Long customerId, Long countryId) throws UnknownPersistenceException, ConstraintViolationException, AccountNotFoundException {
        try {
            System.out.println("ejb.session.stateless.TravelItinerarySessionBean.createNewTravelItinerary()");
            Customer customer = em.find(Customer.class, customerId);
            Country country = em.find(Country.class, countryId);
            if (customer == null || country == null) {
                throw new AccountNotFoundException("Customer or Country not found!");
            }
            travelItinerary.setCountry(country);
            travelItinerary.setCustomer(customer);
            em.persist(travelItinerary);
            customer.getTravelItineraries().add(travelItinerary);
            em.flush();
            return travelItinerary.getTravelItineraryId();
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new ConstraintViolationException("Constrants Violated! ");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }
    
    @Override
    public List<TravelItinerary> retrieveAllTravelItineraries() {
        Query query = em.createQuery("SELECT i from TravelItinerary i");
        return query.getResultList();
    }

    @Override
    public TravelItinerary updateTravelItinerary(TravelItinerary travelItinerary) throws TravelItineraryNotFoundException, UpdateTravelItineraryException {


        TravelItinerary travelItineraryToUpdate = retrieveTravelItineraryById(travelItinerary.getTravelItineraryId());
        if (travelItineraryToUpdate.getBookings() == null || travelItineraryToUpdate.getBookings().size() == 0) {
            travelItineraryToUpdate.setCountry(travelItinerary.getCountry());
        } else if (!travelItinerary.getCountry().getCountryId().equals(travelItineraryToUpdate.getCountry().getCountryId())) {
            throw new UpdateTravelItineraryException("Unable to change country due to existing bookings!");
        }
        if (travelItinerary.getStartDate().after(travelItinerary.getEndDate())) {
            throw new UpdateTravelItineraryException("Start Date must be after End date!");
        }

        Date earliestDate = new Date();
        Date latestDate = new Date();
        Boolean updateStart = false;
        Boolean updateEnd = false;

        for (Booking booking : travelItineraryToUpdate.getBookings()) {
            if (booking.getStartDate().before(earliestDate)) {
                earliestDate.setTime(booking.getStartDate().getTime());
                updateStart = true;
            }
            if (booking.getEndDate().before(latestDate)) {
                latestDate.setTime(booking.getEndDate().getTime());
                updateEnd = true;
            }
            if (booking.getStartDate().before(travelItinerary.getStartDate())
                    || booking.getEndDate().after(travelItinerary.getEndDate())) {
                System.out.println("ejb.session.stateless.TravelItinerarySessionBean.updateTravelItinerary() Booking outside travel itinerary found! Extending start and end date");

            }
        }
//        if (!updateStart || travelItinerary.getStartDate().before(earliestDate)) {
        travelItineraryToUpdate.setStartDate(travelItinerary.getStartDate());
//        } else {
//            travelItineraryToUpdate.setStartDate(earliestDate);
//        }
//        if (!updateEnd || travelItinerary.getEndDate().after(latestDate)) {
        travelItineraryToUpdate.setEndDate(travelItinerary.getEndDate());
//        } else {
//            travelItineraryToUpdate.setEndDate(latestDate);
//        }
        em.flush();
        return travelItineraryToUpdate;
    }

    @Override
    public List<TravelItinerary> retrieveAllCustomerTravelItinerary(Long customerId) throws AccountNotFoundException {
        Customer customer = customerSessionBeanLocal.retrieveCustomerById(customerId);
        Query query = em.createQuery("SELECT ti FROM TravelItinerary ti WHERE ti.customer = :cus");
        query.setParameter("cus", customer);
        return query.getResultList();
    }

    @Override
    public TravelItinerary retrieveTravelItineraryById(Long travelItineraryId) throws TravelItineraryNotFoundException {
        TravelItinerary travelItinerary = em.find(TravelItinerary.class, travelItineraryId);
        if (travelItinerary != null) {
            System.out.println("ejb.session.stateless.TravelItinerarySessionBean.retrieveTravelItineraryById() start = " + travelItinerary.getStartDate() + " end = " + travelItinerary.getEndDate());
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

    //Ensure there are always one hotel/F&B/Entertainment in each country else prepare for errors!
    @Override
    public TravelItinerary recommendTravelItinerary(TravelItinerary travelItinerary) throws ConstraintViolationException, UnknownPersistenceException, CreateNewBookingException {
        System.out.println("ejb.session.stateless.TravelItinerarySessionBean.recommendTravelItinerary()");
        travelItinerary = em.find(TravelItinerary.class, travelItinerary.getTravelItineraryId());
        List<Tag> tags = travelItinerary.getCustomer().getFavouriteTags();
        if (tags == null || tags.size() < 1) {
            tags = tagSessionBeanLocal.retrieveAllTags();
        }

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(travelItinerary.getStartDate());
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.HOUR_OF_DAY, -3);
        endDate.setTime(travelItinerary.getEndDate());
        List<Booking> currBooking = new ArrayList<Booking>(travelItinerary.getBookings());

        System.out.println("tags = " + tags);
        Query query = em.createQuery("SELECT s FROM Service s JOIN s.tags t WHERE s.country = :country AND s.serviceType = :entertainment AND t in :tags GROUP BY s ORDER BY COUNT(s) DESC");
        query.setParameter("tags", tags);
        query.setParameter("country", travelItinerary.getCountry());
        query.setParameter("entertainment", ServiceType.ENTERTAINMENT);

        List<Service> result = query.getResultList();
        /*
        System.out.println("Result Before sort = " + result);
        for(Service s :result){
            System.out.println("Service = " + s.getServiceName());
        }
        
        sortByMostMatches(result, tags);*/

        List<Service> services = new ArrayList<>();

        if (result.size() < 3) {
            services = serviceSessionBeanLocal.retrieveAllEntertainment();
        } else {
            services = result;
        }

        for (Service se : services) {
            System.out.println("Entertainment found = " + se.getServiceName());
        }

        Query query2 = em.createQuery("SELECT s FROM Service s WHERE s.country = :country AND s.serviceType = :hotel");
        query2.setParameter("hotel", ServiceType.HOTEL);
        query2.setParameter("country", travelItinerary.getCountry());
        List<Service> hotels = query2.getResultList();

        for (Service se : hotels) {
            System.out.println("Hotels found = " + se.getServiceName());
        }

        Query query3 = em.createQuery("SELECT s FROM Service s WHERE s.country = :country AND s.serviceType = :FnB ORDER BY s.rating DESC");
        query3.setParameter("FnB", ServiceType.FOOD_AND_BEVERAGE);
        query3.setParameter("country", travelItinerary.getCountry());
        List<Service> FnB = query3.getResultList();

        for (Service se : FnB) {
            System.out.println("FnB found = " + se.getServiceName());
        }

        moveToDaylight(startDate);

        Calendar foodPointer = (Calendar) startDate.clone();
        while (foodPointer.before(endDate)) {
            addMeals(travelItinerary, foodPointer, FnB);
            foodPointer.add(Calendar.DAY_OF_MONTH, 1);
        }

        Calendar entertainmentPointer = (Calendar) startDate.clone();
        while (entertainmentPointer.before(endDate)) {
            addEntertainment(travelItinerary, entertainmentPointer, services);
            entertainmentPointer.add(Calendar.DAY_OF_MONTH, 1);
            moveToDaylight(entertainmentPointer);
        }

        Calendar hotelPointer = (Calendar) startDate.clone();
        while (hotelPointer.before(endDate)) {
            addHotels(travelItinerary, hotelPointer, hotels);
            hotelPointer.add(Calendar.DAY_OF_MONTH, 1);
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
                Calendar midnight = (Calendar) entertainmentPointer.clone();
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
        System.out.println("ejb.session.stateless.TravelItinerarySessionBean.addHotels()");

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
        startDate.set(Calendar.MINUTE, 0);
        endDate.set(Calendar.HOUR_OF_DAY, 12);
        endDate.set(Calendar.MINUTE, 0);
        endDate.add(Calendar.DAY_OF_MONTH, 1);

        Service hotel = hotels.remove(0);
        hotels.add(hotel);
        Booking newBooking = new Booking(startDate.getTime(), endDate.getTime(), ti, hotel);
        bookingSessionBeanLocal.createBooking(newBooking, hotel.getServiceId(), ti.getTravelItineraryId());
    }

    private void addMeals(TravelItinerary ti, Calendar day, List<Service> meals) throws ConstraintViolationException, UnknownPersistenceException, CreateNewBookingException {
        System.out.println("ejb.session.stateless.TravelItinerarySessionBean.addMeals() at day " + day.get(Calendar.DAY_OF_MONTH) + " month = " + day.get(Calendar.MONTH));
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
                //System.out.println("Lunch Overlap found!");
                lunchOverlap = true;
                if (booking.getService().getServiceType() == ServiceType.FOOD_AND_BEVERAGE) {
                    hadLunch = true;
                }
            } else if (formatter.before(dinnerEnd) || formatter2.after(dinnerStart)) {
                dinnerOverlap = true;
                //System.out.println("Dinner Overlap found!");
                if (booking.getService().getServiceType() == ServiceType.FOOD_AND_BEVERAGE) {
                    hadDinner = true;
                }
            }
        }
        //System.out.println("checking for lunch Had Lunch = " + hadLunch);
        if (!hadLunch) {
            Calendar noon = Calendar.getInstance();
            noon.setTime(day.getTime());
            noon.set(Calendar.HOUR_OF_DAY, 12);
            //System.out.println("checking for lunch overlap = " + lunchOverlap);
            if (!lunchOverlap) {

                Service lunch = meals.remove(0);
                meals.add(lunch);
                Calendar lunchS = Calendar.getInstance();
                lunchS.setTime(day.getTime());
                lunchS.set(Calendar.HOUR_OF_DAY, 12);
                Calendar lunchE = (Calendar) lunchS.clone();
                lunchE.add(Calendar.HOUR_OF_DAY, 1);
                Booking newBooking = new Booking(lunchS.getTime(), lunchE.getTime(), ti, lunch);
                //System.out.println("Add lunch without overlap! name = " + lunch.getServiceName() + " from " + lunchS.getTime() + " to " + lunchE.getTime());
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
                        //System.out.println("Added lunch with overlap! name = " + lunch.getServiceName() + " from " + startDate + " to " + endDate);
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
                        //System.out.println("Added lunch with overlap!");
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
                        //System.out.println("Added lunch with overlap!");
                        bookingSessionBeanLocal.createBooking(newBooking, lunch.getServiceId(), ti.getTravelItineraryId());
                        hadLunch = true;
                        break;
                    }
                }
            }
        }
        //System.out.println("checking for dinner Had Dinner = " + hadDinner);
        if (!hadDinner) {

            if (!dinnerOverlap) {
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
        formatter.set(Calendar.MINUTE, 0);
        formatter.set(Calendar.MILLISECOND, 0);
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
                try {
                    BigDecimal price = bookingSessionBeanLocal.getPricingOfBooking(booking.getBookingId(), booking.getStartDate(), booking.getEndDate());
                    totalPrice = totalPrice.add(price);
                } catch (Exception exception) {
                    System.out.println("This should not happen " + exception.getMessage());
                }
            }
        }
        return totalPrice;
    }

    private List<Service> sortByMostMatches(List<Service> result, List<Tag> tags) {
        HashMap<Service, Integer> map = new HashMap<>();
        for (Tag t : tags) {
            for (Service s : result) {
                if (t.getServices().contains(s)) {
                    Integer count = map.get(s);
                    if (count != null) {
                        map.put(s, count + 1);
                    } else {
                        map.put(s, 1);
                    }
                }
            }
        }
        List<Entry<Service, Integer>> newList = new ArrayList<>(map.entrySet());
        newList.sort((a, b) -> a.getValue().compareTo(b.getValue()));
        List<Service> output = new LinkedList<>();
        for (Entry<Service, Integer> entry : newList) {
            output.add(entry.getKey());
        }
        return output;
    }
}

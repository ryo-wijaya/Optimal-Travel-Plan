/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Booking;
import entity.Country;
import entity.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CountryNotFoundException;
import util.exception.DeleteCountryException;

@Stateless
public class CountrySessionBean implements CountrySessionBeanLocal {

    @EJB
    private BookingSessionBeanLocal bookingSessionBeanLocal;

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;

    @Override
    public Country createNewCountry(Country newCountry) {
        em.persist(newCountry);
        em.flush();
        return newCountry;
    }

    @Override
    public List<Country> retrieveAllCountries() {
        Query query = em.createQuery("SELECT c FROM Country c ORDER BY c.name ASC");
        List<Country> tagEntities = query.getResultList();

        for (Country tagEntity : tagEntities) {
            tagEntity.getServices().size();
        }
        return tagEntities;
    }

    @Override
    public Country retrieveCountryByCountryId(Long countryId) throws CountryNotFoundException {
        Country country = em.find(Country.class, countryId);
        if (country != null) {
            return country;
        } else {
            throw new CountryNotFoundException("Country ID " + countryId + " does not exist!");
        }
    }

    @Override
    public void deleteCountry(Long countryId) throws DeleteCountryException {
        Country countryEntityToRemove = em.find(Country.class, countryId);
        if (!countryEntityToRemove.getServices().isEmpty()) {
            throw new DeleteCountryException("Country ID " + countryId + " is associated with existing services and cannot be deleted!");
        } else {
            em.remove(countryEntityToRemove);
        }
    }

    @Override
    public Country updateCountry(Country country) {
        Country countryToUpdate = em.find(Country.class, country.getCountryId());
        countryToUpdate.setName(country.getName());
        if (countryToUpdate.getServices() != null) {
            countryToUpdate.setServices(country.getServices());
        }
        em.flush();
        return countryToUpdate;
    }

    @Override
    public String mostPopularCountry() {
        Map<String, Integer> map = new HashMap<>();

        List<Booking> bookings = bookingSessionBeanLocal.retrieveAllBookings();

        for (Booking booking : bookings) {
            if (!map.containsKey(booking.getService().getCountry().getName())) {
                map.put(booking.getService().getCountry().getName(), 1);
            } else {
                map.put(booking.getService().getCountry().getName(), map.get(booking.getService().getCountry().getName()) + 1);
            }
        }

        Integer maxCount = 0;
        String countryToReturn = "";

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (maxCount < entry.getValue()) {
                maxCount = entry.getValue();
                countryToReturn = entry.getKey();
            }
        }
        return countryToReturn;
    }

    public void persist(Object object) {
        em.persist(object);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.BookingSessionBeanLocal;
import ejb.session.stateless.CountrySessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.PaymentAccountSessionBeanLocal;
import ejb.session.stateless.ReviewSessionBeanLocal;
import ejb.session.stateless.ServiceRateSessionBeanLocal;
import ejb.session.stateless.ServiceSessionBeanLocal;
import ejb.session.stateless.SupportRequestSessionBeanLocal;
import ejb.session.stateless.TagSessionBeanLocal;
import ejb.session.stateless.TransactionSessionBeanLocal;
import ejb.session.stateless.TravelItinerarySessionBeanLocal;
import entity.Booking;
import entity.Business;
import entity.Country;
import entity.Customer;
import entity.PaymentAccount;
import entity.PaymentTransaction;
import entity.Review;
import entity.Service;
import entity.ServiceRate;
import entity.Staff;
import entity.SupportRequest;
import entity.Tag;
import entity.TravelItinerary;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.ChargeType;
import util.enumeration.EmployeeRole;
import util.enumeration.PaymentType;
import util.enumeration.RateType;
import util.enumeration.ServiceType;
import util.exception.AccountNotFoundException;
import util.exception.BookingNotFoundException;
import util.exception.ConstraintViolationException;
import util.exception.CreateNewBookingException;
import util.exception.CreateNewServiceException;
import util.exception.CreateNewServiceRateException;
import util.exception.CreateSupportRequestException;
import util.exception.PasswordNotAcceptedException;
import util.exception.ServiceNotFoundException;
import util.exception.TagAlreadyExistException;
import util.exception.TagNotFoundException;
import util.exception.UnknownPersistenceException;

@Singleton
@LocalBean
@Startup
public class dataInitBean {

    @EJB
    private PaymentAccountSessionBeanLocal paymentAccountSessionBeanLocal;

    @EJB
    private TransactionSessionBeanLocal transactionSessionBeanLocal;

    @EJB
    private ReviewSessionBeanLocal reviewSessionBeanLocal;

    @EJB
    private SupportRequestSessionBeanLocal supportRequestSessionBeanLocal;

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    @EJB
    private TravelItinerarySessionBeanLocal travelItinerarySessionBeanLocal;

    @EJB
    private BookingSessionBeanLocal bookingSessionBeanLocal;

    @EJB
    private ServiceRateSessionBeanLocal serviceRateSessionBeanLocal;

    @EJB
    private ServiceSessionBeanLocal serviceSessionBeanLocal;

    @EJB
    private CountrySessionBeanLocal countrySessionBeanLocal;

    @EJB
    private TagSessionBeanLocal tagSessionBeanLocal;

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;

    public dataInitBean() {
    }

    @PostConstruct
    public void postConstruct() {
        if (em.createQuery("SELECT s FROM Staff s").getResultList().size() < 1) {
            try {
                //default manager
                Staff manager = new Staff("manager1", "password", "admin1");
                em.persist(manager);

                Staff customerService = new Staff("staff123", "password", "staff1");
                em.persist(customerService);

                Business optimalTravelPlan = new Business("OTP themselves!", "www.OTP.com", "99999999", "OTP address", "optimal123", "password", "emailAdress");
                em.persist(optimalTravelPlan);
                em.flush();

                Business business1 = new Business("company1", "www.company1.com", "0000001", "address1", "company1", "password", "emailAdress");
                em.persist(business1);

                Business business2 = new Business("company2", "www.company2.com", "0000002", "address2", "company2", "password", "emailAdress");
                em.persist(business2);

                Business business3 = new Business("company3", "www.company3.com", "0000003", "address3", "company3", "password", "emailAdress");
                em.persist(business3);

                Business business4 = new Business("company4", "www.company4.com", "0000004", "address4", "company4", "password", "emailAdress");
                em.persist(business4);

                Business business5 = new Business("company5", "www.company5.com", "0000005", "address5", "company5", "password", "emailAdress");
                em.persist(business5);

                Customer customer1 = new Customer("customer1", "123456789", "000000001", "customer1@email.com", Boolean.TRUE, "customer1", "password");
                em.persist(customer1);

                //Create data here
                Tag familyTag = tagSessionBeanLocal.createNewTag(new Tag("family"));
                Tag natureTag = tagSessionBeanLocal.createNewTag(new Tag("nature"));
                Tag cultureTag = tagSessionBeanLocal.createNewTag(new Tag("culture"));
                Tag nightTag = tagSessionBeanLocal.createNewTag(new Tag("night"));
                Tag testTag = tagSessionBeanLocal.createNewTag(new Tag("empty test tag"));

                customerSessionBeanLocal.associateTagToCustomer(customer1.getAccountId(), familyTag.getTagId());
                customerSessionBeanLocal.associateTagToCustomer(customer1.getAccountId(), natureTag.getTagId());
                customerSessionBeanLocal.associateTagToCustomer(customer1.getAccountId(), cultureTag.getTagId());
                customerSessionBeanLocal.associateTagToCustomer(customer1.getAccountId(), nightTag.getTagId());

                List<Long> tagList1 = new ArrayList<>();
                tagList1.add(familyTag.getTagId());
                tagList1.add(natureTag.getTagId());
                tagList1.add(cultureTag.getTagId());

                List<Long> tagList2 = new ArrayList<>();
                tagList2.add(natureTag.getTagId());
                tagList2.add(cultureTag.getTagId());

                List<Long> tagList3 = new ArrayList<>();
                tagList3.add(cultureTag.getTagId());

                List<Long> tagList4 = new ArrayList<>();
                tagList4.add(nightTag.getTagId());

                List<Long> tagList5 = new ArrayList<>();
                tagList5.add(testTag.getTagId());

                Country singapore = countrySessionBeanLocal.createNewCountry(new Country("Singapore"));
                Country japan = countrySessionBeanLocal.createNewCountry(new Country("Japan"));
                Country Taiwan = countrySessionBeanLocal.createNewCountry(new Country("Taiwan"));

                Long service1 = serviceSessionBeanLocal.createNewService(new Service(business1, singapore, ServiceType.HOTEL, Boolean.TRUE, "address1", "Melion Hotel"), business1.getBusinessId(), tagList1, singapore.getCountryId());
                Long ServiceRate1 = serviceRateSessionBeanLocal.createNewServiceRate(new ServiceRate(new Date(100, 02, 26), new Date(2088, 03, 26), BigDecimal.valueOf(500.00), RateType.NORMAL, Boolean.TRUE, ChargeType.ENTRY), service1);

                Long service2 = serviceSessionBeanLocal.createNewService(new Service(business2, singapore, ServiceType.FOOD_AND_BEVERAGE, Boolean.TRUE, "address2", "Encik Tan"), business2.getBusinessId(), tagList2, singapore.getCountryId());
                Long ServiceRate2 = serviceRateSessionBeanLocal.createNewServiceRate(new ServiceRate(new Date(100, 02, 26), new Date(2088, 03, 26), BigDecimal.valueOf(100.00), RateType.NORMAL, Boolean.TRUE, ChargeType.ENTRY), service2);
                em.flush();

                Long service3 = serviceSessionBeanLocal.createNewService(new Service(business3, singapore, ServiceType.ENTERTAINMENT, Boolean.TRUE, "address3", "Singapore Flyer"), business3.getBusinessId(), tagList3, singapore.getCountryId());
                Long ServiceRate3 = serviceRateSessionBeanLocal.createNewServiceRate(new ServiceRate(new Date(100, 02, 26), new Date(2088, 03, 26), BigDecimal.valueOf(100.00), RateType.NORMAL, Boolean.TRUE, ChargeType.ENTRY), service3);
                em.flush();

                Long service4 = serviceSessionBeanLocal.createNewService(new Service(business4, singapore, ServiceType.ENTERTAINMENT, Boolean.TRUE, "address4", "Singapore Zoo"), business4.getBusinessId(), tagList4, singapore.getCountryId());
                Long ServiceRate4 = serviceRateSessionBeanLocal.createNewServiceRate(new ServiceRate(new Date(100, 02, 26), new Date(2088, 03, 26), BigDecimal.valueOf(10.00), RateType.NORMAL, Boolean.TRUE, ChargeType.ENTRY), service4);
                em.flush();

                Long service5 = serviceSessionBeanLocal.createNewService(new Service(business5, singapore, ServiceType.FOOD_AND_BEVERAGE, Boolean.TRUE, "address5", "Gong Cha"), business5.getBusinessId(), tagList5, singapore.getCountryId());
                Long ServiceRate5 = serviceRateSessionBeanLocal.createNewServiceRate(new ServiceRate(new Date(100, 02, 26), new Date(2088, 03, 26), BigDecimal.valueOf(100.00), RateType.NORMAL, Boolean.TRUE, ChargeType.ENTRY), service5);
                em.flush();

                Long service6 = serviceSessionBeanLocal.createNewService(new Service(business4, singapore, ServiceType.ENTERTAINMENT, Boolean.TRUE, "address6", "Sentosa"), business4.getBusinessId(), tagList2, singapore.getCountryId());
                Long ServiceRate6 = serviceRateSessionBeanLocal.createNewServiceRate(new ServiceRate(new Date(100, 02, 26), new Date(2088, 03, 26), BigDecimal.valueOf(69.00), RateType.NORMAL, Boolean.TRUE, ChargeType.HOURLY), service6);
                em.flush();

                Long service7 = serviceSessionBeanLocal.createNewService(new Service(business4, singapore, ServiceType.ENTERTAINMENT, Boolean.TRUE, "address7", "NUS Tour"), business4.getBusinessId(), tagList1, singapore.getCountryId());
                Long ServiceRate7 = serviceRateSessionBeanLocal.createNewServiceRate(new ServiceRate(new Date(100, 02, 26), new Date(2088, 03, 26), BigDecimal.valueOf(999999.00), RateType.NORMAL, Boolean.TRUE, ChargeType.ENTRY), service7);
                em.flush();

                Long service8 = serviceSessionBeanLocal.createNewService(new Service(business1, singapore, ServiceType.ENTERTAINMENT, Boolean.TRUE, "address8", "Experiemental Test"), business1.getBusinessId(), tagList5, singapore.getCountryId());
                Long ServiceRate8 = serviceRateSessionBeanLocal.createNewServiceRate(new ServiceRate(new Date(100, 02, 26), new Date(2088, 03, 26), BigDecimal.valueOf(0.01), RateType.NORMAL, Boolean.TRUE, ChargeType.ENTRY), service8);
                em.flush();

                Date startDate = new Date();
                Date endDate = new Date();
                endDate.setTime(endDate.getTime() + 116000000l);
                Date endDate2 = new Date();
                endDate2.setTime(endDate.getTime() + 1000*60*60*24*3l);

                TravelItinerary travelItinerary1 = new TravelItinerary(customer1, startDate, endDate, singapore);
                TravelItinerary travelItinerary2 = new TravelItinerary(customer1, startDate, endDate2, singapore);
                TravelItinerary travelItinerary3 = new TravelItinerary(customer1, startDate, endDate2, singapore);
                TravelItinerary travelItinerary4 = new TravelItinerary(customer1, startDate, endDate2, singapore);
                TravelItinerary travelItinerary5 = new TravelItinerary(customer1, startDate, endDate2, singapore);
                TravelItinerary travelItinerary6 = new TravelItinerary(customer1, startDate, endDate2, singapore);
                TravelItinerary travelItinerary7 = new TravelItinerary(customer1, startDate, endDate2, singapore);
                TravelItinerary travelItinerary8 = new TravelItinerary(customer1, startDate, endDate2, singapore);
                TravelItinerary travelItinerary9 = new TravelItinerary(customer1, startDate, endDate2, singapore);

                Long travel1 = travelItinerarySessionBeanLocal.createNewTravelItinerary(travelItinerary1, customer1.getAccountId(), singapore.getCountryId());
                Long travel2 = travelItinerarySessionBeanLocal.createNewTravelItinerary(travelItinerary2, customer1.getAccountId(), singapore.getCountryId());
                Long travel3 = travelItinerarySessionBeanLocal.createNewTravelItinerary(travelItinerary3, customer1.getAccountId(), singapore.getCountryId());
                Long travel4 = travelItinerarySessionBeanLocal.createNewTravelItinerary(travelItinerary4, customer1.getAccountId(), singapore.getCountryId());
                Long travel5 = travelItinerarySessionBeanLocal.createNewTravelItinerary(travelItinerary5, customer1.getAccountId(), singapore.getCountryId());
                Long travel6 = travelItinerarySessionBeanLocal.createNewTravelItinerary(travelItinerary6, customer1.getAccountId(), singapore.getCountryId());
                Long travel7 = travelItinerarySessionBeanLocal.createNewTravelItinerary(travelItinerary7, customer1.getAccountId(), singapore.getCountryId());
                Long travel8 = travelItinerarySessionBeanLocal.createNewTravelItinerary(travelItinerary8, customer1.getAccountId(), singapore.getCountryId());
                Long travel9 = travelItinerarySessionBeanLocal.createNewTravelItinerary(travelItinerary9, customer1.getAccountId(), singapore.getCountryId());
                em.flush();

                travelItinerary1 = travelItinerarySessionBeanLocal.recommendTravelItinerary(travelItinerary1);

                System.out.println("travel Itin = " + travelItinerary1);
                System.out.println("travel Itin booking = " + travelItinerary1.getBookings().size());
                List<Booking> list = travelItinerary1.getBookings();

                PaymentAccount account = new PaymentAccount("accoutNumber", new Date(), "123", PaymentType.VISA, true);
                paymentAccountSessionBeanLocal.createNewPaymentAccount(customer1.getCustomerId(), account);

                for (int i = 0; i < list.size(); i++) {
                    Booking booking = list.get(i);
                    
                    //NEWLY ADDED LINE - FOR FIRST BOOKING, NO SUPPORT REQUEST OR REVIEW OR ANYTHING
                    if (i == 0) {
                        continue;
                    }
                    
                    String dateFormat = supportRequestSessionBeanLocal.getFormattedComment(booking.getTravelItinerary().getCustomer().getName());
                    SupportRequest supportRequest1 = new SupportRequest(dateFormat + "I am not happy with the world :'(\n", new Date(), booking);

                    Review review1 = new Review(5, "This is the review content");

                    PaymentTransaction transaction = new PaymentTransaction(account, endDate, "transaction number", BigDecimal.TEN);

                    reviewSessionBeanLocal.createNewReview(booking.getBookingId(), review1);
                    transactionSessionBeanLocal.createNewPaymentTransaction(transaction, booking.getBookingId());

                    supportRequestSessionBeanLocal.createNewSupportRequest(supportRequest1, booking.getBookingId());

                    System.out.println("i = " + i + " list[i] = " + booking);
                    System.out.println("booking id = " + booking.getBookingId());
                    System.out.println("booking start = " + booking.getStartDate());
                    System.out.println("booking end = " + booking.getEndDate());
                    System.out.println("Service name = " + booking.getService().getServiceName());
                    System.out.println("\n...");
                }

            } catch (BookingNotFoundException | CreateSupportRequestException | TagNotFoundException | CreateNewBookingException | AccountNotFoundException | TagAlreadyExistException | UnknownPersistenceException | ConstraintViolationException | CreateNewServiceException | CreateNewServiceRateException | PasswordNotAcceptedException ex) {
                for (int i = 0; i < 30; i++) {
                    System.out.println("Error in init bean!" + ex.getMessage());
                }
            }

            em.flush();
        }
    }
}

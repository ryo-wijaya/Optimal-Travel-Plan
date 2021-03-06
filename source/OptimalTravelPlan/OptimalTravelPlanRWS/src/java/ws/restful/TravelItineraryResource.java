/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.restful;

import ejb.session.stateless.AccountSessionBeanLocal;
import ejb.session.stateless.BookingSessionBeanLocal;
import ejb.session.stateless.CountrySessionBeanLocal;
import ejb.session.stateless.EmailSessionBeanLocal;
import ejb.session.stateless.PaymentAccountSessionBeanLocal;
import ejb.session.stateless.TagSessionBeanLocal;
import ejb.session.stateless.TravelItinerarySessionBeanLocal;
import entity.Booking;
import entity.Country;
import entity.Customer;
import entity.PaymentAccount;
import entity.Tag;
import entity.TravelItinerary;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import util.exception.AccountDisabledException;
import util.exception.CustomerNotMatchException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;
import ws.DataModel.TravelItineraryHandler;

/**
 * REST Web Service
 *
 * @author sucram
 */
@Path("TravelItinerary")
public class TravelItineraryResource {

    EmailSessionBeanLocal emailSessionBeanLocal = lookupEmailSessionBeanLocal();

    BookingSessionBeanLocal bookingSessionBeanLocal = lookupBookingSessionBeanLocal();

    PaymentAccountSessionBeanLocal paymentAccountSessionBeanLocal = lookupPaymentAccountSessionBeanLocal();

    CountrySessionBeanLocal countrySessionBeanLocal = lookupCountrySessionBeanLocal();
    

    TagSessionBeanLocal tagSessionBeanLocal = lookupTagSessionBeanLocal();

    TravelItinerarySessionBeanLocal travelItinerarySessionBeanLocal = lookupTravelItinerarySessionBeanLocal();

    AccountSessionBeanLocal accountSessionBeanLocal = lookupAccountSessionBeanLocal();
    
    
    
    

    private void convertDate(TravelItineraryHandler objHandler) {
        if (objHandler.getStartDate() != null && objHandler.getEndDate() != null) {
            System.out.println("ws.restful.TravelItineraryResource.convertDate()");
            Date s = new Date(objHandler.getStartDate());
            Date e = new Date(objHandler.getEndDate());

            objHandler.getTravelItinerary().setStartDate(s);
            objHandler.getTravelItinerary().setEndDate(e);
        }

    }

    @Context
    private UriInfo context;

    public TravelItineraryResource() {
    }

    @Path("Create")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTravelItinerary(TravelItineraryHandler objHandler) {
        if (objHandler != null) {
            try {
                System.out.println("ws.restful.TravelItineraryResource.createTravelItinerary()");
                Customer customer = (Customer) accountSessionBeanLocal.login(objHandler.getCustomer().getUsername(), objHandler.getPassword());
                objHandler.getTravelItinerary().getBookings().clear();
                convertDate(objHandler);
                Long travelItineraryId
                        = travelItinerarySessionBeanLocal.createNewTravelItinerary(
                                objHandler.getTravelItinerary(),
                                customer.getCustomerId(),
                                objHandler.getNewCountryId());

                return Response.status(Response.Status.OK).entity(travelItineraryId).build();

            } catch (InvalidLoginCredentialException | AccountDisabledException | UnknownPersistenceException ex) {
                return Response.status(Response.Status.FORBIDDEN).entity(ex.getMessage()).build();
            } catch (Exception ex) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid update request").build();
        }
    }

    @Path("Update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTravelItinerary(TravelItineraryHandler objHandler) {
        if (objHandler != null) {
            try {

                Customer customer = (Customer) accountSessionBeanLocal.login(objHandler.getCustomer().getUsername(), objHandler.getPassword());
                TravelItinerary travelItinerary = objHandler.getTravelItinerary();

                convertDate(objHandler);
                System.out.println("ws.restful.TravelItineraryResource.updateTravelItinerary() end date " + travelItinerary.getEndDate());
                System.out.println("ws.restful.TravelItineraryResource.updateTravelItinerary()start date " + travelItinerary.getStartDate());

                if (travelItinerary.getTravelItineraryId() == null) {
                    travelItinerary.setTravelItineraryId(objHandler.getTravelItineraryId());

                    System.out.println("ws.restful.TravelItineraryResource.updateTravelItinerary() setting back travel itin id " + travelItinerary.getTravelItineraryId());
                }
                Long ownerID = travelItinerarySessionBeanLocal.retrieveTravelItineraryById(travelItinerary.getTravelItineraryId()).getCustomer().getCustomerId();

                if (!customer.getAccountId().equals(ownerID)) {
                    throw new CustomerNotMatchException("Please ensure travel itinerary matches customer!");
                }

                System.out.println("ws.restful.TravelItineraryResource.updateTravelItinerary()" + travelItinerary.getTravelItineraryId());
                travelItinerary = travelItinerarySessionBeanLocal.updateTravelItinerary(travelItinerary);
                
                for (Booking booking: objHandler.getTravelItinerary().getBookings()){
                    if(booking.getBookingId() == null){
                        bookingSessionBeanLocal.createBooking(booking, booking.getService().getServiceId(), travelItinerary.getTravelItineraryId());
                    }
                }
                travelItinerary = travelItinerarySessionBeanLocal.retrieveTravelItineraryById(travelItinerary.getTravelItineraryId());
                travelItinerary.cleanRelationships();

                System.out.println("ws.restful.TravelItineraryResource.updateTravelItinerary() new start = " + travelItinerary.getStartDate() + " new end = " + travelItinerary.getEndDate());

                return Response.status(Response.Status.OK).entity(travelItinerary).build();

            } catch (Exception ex) {
                ex.printStackTrace();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid update request").build();
        }
    }

    @Path("RetrieveCustomerTravelItinerary")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllTravelItinerary(@QueryParam("username") String username,
            @QueryParam("password") String password) {
        try {
            Customer customer = (Customer) accountSessionBeanLocal.login(username, password);
            List<TravelItinerary> list = travelItinerarySessionBeanLocal.retrieveAllCustomerTravelItinerary(customer.getCustomerId());
            for (TravelItinerary ti : list) {
                ti.cleanRelationships();
            }
            GenericEntity<List<TravelItinerary>> genericEntity = new GenericEntity<List<TravelItinerary>>(list) {
            };
            return Response.status(Status.OK).entity(genericEntity).build();
        } catch (InvalidLoginCredentialException ex) {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @Path("RecommendTravelItinerary/{travelItineraryId}")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response recommendTravelItinerary(@QueryParam("username") String username,
            @QueryParam("password") String password,
            @PathParam("travelItineraryId") Long travelItineraryId) {
        try {

            System.out.println("ws.restful.TravelItineraryResource.recommendTravelItinerary()");
            Customer customer = (Customer) accountSessionBeanLocal.login(username, password);
            if (!customer.getCustomerId().equals(travelItinerarySessionBeanLocal.retrieveTravelItineraryById(travelItineraryId).getCustomer().getCustomerId())) {
                throw new CustomerNotMatchException("Please ensure travel itinerary matches customer!");
            }
            TravelItinerary ti = travelItinerarySessionBeanLocal.retrieveTravelItineraryById(travelItineraryId);
            
            
            ti = travelItinerarySessionBeanLocal.recommendTravelItinerary(ti);
            ti.cleanRelationships();
            ResponseBuilder r = Response.status(Status.OK);
            r = r.entity(ti);
            Response k = r.build();
            return k;
        } catch (InvalidLoginCredentialException ex) {
            System.out.println("ws.restful.TravelItineraryResource.recommendTravelItinerary() error = " + ex.getMessage());
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            System.out.println("ws.restful.TravelItineraryResource.recommendTravelItinerary() error = " + ex.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @Path("Delete/{travelItineraryId}")
    @DELETE
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTravelItinerary(@QueryParam("username") String username,
            @QueryParam("password") String password,
            @PathParam("travelItineraryId") Long travelItineraryId) {
        try {
            Customer customer = (Customer) accountSessionBeanLocal.login(username, password);
            if (!customer.getCustomerId().equals(travelItinerarySessionBeanLocal.retrieveTravelItineraryById(travelItineraryId).getCustomer().getCustomerId())) {
                throw new CustomerNotMatchException("Please ensure travel itinerary matches customer!");
            }
            travelItinerarySessionBeanLocal.deleteTravelItinerary(travelItineraryId);
            return Response.status(Status.OK).entity(Boolean.TRUE).build();
        } catch (Exception ex) {
            return Response.status(Status.METHOD_NOT_ALLOWED).entity(ex.getMessage()).build();
        }
    }

    @Path("RetrieveAllTags")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllTags() {
        try {
            List<Tag> tags = tagSessionBeanLocal.retrieveAllTags();
            for (Tag t : tags) {
                t.cleanRelationships();
            }
            GenericEntity<List<Tag>> genericEntity = new GenericEntity<List<Tag>>(tags) {
            };
            return Response.status(Status.OK).entity(genericEntity).build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @Path("RetrieveAllCountries")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllCountries() {
        try {
            System.out.println("ws.restful.TravelItineraryResource.retrieveAllCountries()");
            List<Country> countries = countrySessionBeanLocal.retrieveAllCountries();
            for (Country t : countries) {
                t.cleanRelationships();
            }
            GenericEntity<List<Country>> genericEntity = new GenericEntity<List<Country>>(countries) {
            };
            return Response.status(Status.OK).entity(genericEntity).build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @Path("PayForAllBookings")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response payForAllBookings(@QueryParam("username") String username, @QueryParam("password") String password,
            @QueryParam("PaymentAccountId") String paymentAccountNumber,
            @QueryParam("TravelItineraryId") Long travelItineraryId) {
        try {
            Customer customer = (Customer) accountSessionBeanLocal.login(username, password);
            TravelItinerary ti = travelItinerarySessionBeanLocal.retrieveTravelItineraryById(travelItineraryId);
            
            if (!ti.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
                throw new CustomerNotMatchException("Please ensure booking matches customer!");
            }
            Long paymentAccountId = 0l;
            for(PaymentAccount pa : customer.getPaymentAccounts()){
                if(pa.getAccountNumber().equals(paymentAccountNumber)){
                    paymentAccountId = pa.getPaymenetAccountId();
                    break;
                }
            }
            
            ti = travelItinerarySessionBeanLocal.payForAllBookings(travelItineraryId, paymentAccountId);
            ti = travelItinerarySessionBeanLocal.retrieveTravelItineraryById(ti.getTravelItineraryId());
            ti.cleanRelationships();
            
            String message = "Dear user" + ",\n\n"
                + "This is the confirm the payment for all bookings in the travel itinerary with ID " + travelItineraryId + " starting from "
                    + ti.getStartDate() + " to " + ti.getEndDate() + "\n\n" + "Thank you for using Optimal Travel Plan for your travel needs!";
            
            try {
                emailSessionBeanLocal.emailCheckoutNotificationAsync(message, customer.getEmail());
            } catch (InterruptedException ex) {
                System.out.println("Email sending failed");
            }

            return Response.status(Status.OK).entity(ti).build();
        } catch (Exception ex) {
            System.out.println("ws.restful.BookingResource.retrieveBookingById() error = " + ex.getLocalizedMessage());
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        }
    }

    @Path("CalculateTotalItineraryPrice")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response calculateTotalItineraryPrice(@QueryParam("username") String username, @QueryParam("password") String password,
            @QueryParam("TravelItineraryId") Long travelItineraryId) {
        try {
            Customer customer = (Customer) accountSessionBeanLocal.login(username, password);
            TravelItinerary ti = travelItinerarySessionBeanLocal.retrieveTravelItineraryById(travelItineraryId);
            if (!ti.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
                throw new CustomerNotMatchException("Please ensure booking matches customer!");
            }
            BigDecimal subtotal = travelItinerarySessionBeanLocal.calculateTotalItineraryPrice(ti);
            

            return Response.status(Status.OK).entity(subtotal).build();
        } catch (Exception ex) {
            System.out.println("ws.restful.BookingResource.retrieveBookingById() error = " + ex.getLocalizedMessage());
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        }
    }

    private AccountSessionBeanLocal lookupAccountSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (AccountSessionBeanLocal) c.lookup("java:global/OptimalTravelPlan/OptimalTravelPlan-ejb/AccountSessionBean!ejb.session.stateless.AccountSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private TravelItinerarySessionBeanLocal lookupTravelItinerarySessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (TravelItinerarySessionBeanLocal) c.lookup("java:global/OptimalTravelPlan/OptimalTravelPlan-ejb/TravelItinerarySessionBean!ejb.session.stateless.TravelItinerarySessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private TagSessionBeanLocal lookupTagSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (TagSessionBeanLocal) c.lookup("java:global/OptimalTravelPlan/OptimalTravelPlan-ejb/TagSessionBean!ejb.session.stateless.TagSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private CountrySessionBeanLocal lookupCountrySessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (CountrySessionBeanLocal) c.lookup("java:global/OptimalTravelPlan/OptimalTravelPlan-ejb/CountrySessionBean!ejb.session.stateless.CountrySessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private PaymentAccountSessionBeanLocal lookupPaymentAccountSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (PaymentAccountSessionBeanLocal) c.lookup("java:global/OptimalTravelPlan/OptimalTravelPlan-ejb/PaymentAccountSessionBean!ejb.session.stateless.PaymentAccountSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private BookingSessionBeanLocal lookupBookingSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (BookingSessionBeanLocal) c.lookup("java:global/OptimalTravelPlan/OptimalTravelPlan-ejb/BookingSessionBean!ejb.session.stateless.BookingSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private EmailSessionBeanLocal lookupEmailSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (EmailSessionBeanLocal) c.lookup("java:global/OptimalTravelPlan/OptimalTravelPlan-ejb/EmailSessionBean!ejb.session.stateless.EmailSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.restful;

import ejb.session.stateless.AccountSessionBeanLocal;
import ejb.session.stateless.BookingSessionBeanLocal;
import entity.Booking;
import entity.Customer;
import entity.TravelItinerary;
import java.math.BigDecimal;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import util.exception.BookingNotFoundException;
import util.exception.CustomerNotMatchException;
import ws.DataModel.BookingHandler;

/**
 * REST Web Service
 *
 * @author sucram
 */
@Path("Booking")
public class BookingResource {

    AccountSessionBeanLocal accountSessionBeanLocal = lookupAccountSessionBeanLocal();

    BookingSessionBeanLocal bookingSessionBeanLocal = lookupBookingSessionBeanLocal();

    @Context
    private UriInfo context;

    public BookingResource() {
    }

    @Path("Create")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBooking(BookingHandler objHandler) {
        if (objHandler != null) {
            try {
                System.out.println("ws.restful.BookingResource.createBooking()");
                Customer customer = (Customer) accountSessionBeanLocal.login(objHandler.getCustomer().getUsername(), objHandler.getPassword());
                Long bookingId = bookingSessionBeanLocal.createBooking(objHandler.getBooking(), objHandler.getServiceId(), objHandler.getTravelItineraryId());
                return Response.status(Response.Status.OK).entity(bookingId).build();
            } catch (Exception ex) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid update request").build();
        }
    }

    @Path("RetrieveBookingById/{bookingId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveBookingById(@QueryParam("username") String username, @QueryParam("password") String password,
            @PathParam("bookingId") Long bookingId) {
        try {
            Customer customer = (Customer) accountSessionBeanLocal.login(username, password);
            System.out.println("ws.restful.BookingResource.retrieveBookingById()booking id = " + bookingId);
            Booking booking = bookingSessionBeanLocal.retrieveBookingById(bookingId);
            System.out.println("ws.restful.BookingResource.retrieveBookingById() Retrieved successfully!");
            if (!booking.getTravelItinerary().getCustomer().getCustomerId().equals(customer.getCustomerId())) {
                throw new CustomerNotMatchException("Please ensure booking matches customer!");
            }
            booking.cleanRelationships();

            System.out.println("ws.restful.BookingResource.retrieveBookingById() Calling get price");
            BigDecimal cost = bookingSessionBeanLocal.getPricingOfBooking(bookingId, booking.getStartDate(), booking.getEndDate());
            BookingHandler handler = new BookingHandler();

            handler.setBooking(booking);
            handler.setCost(cost);

            return Response.status(Status.OK).entity(handler).build();
        } catch (Exception ex) {
            System.out.println("ws.restful.BookingResource.retrieveBookingById() error = " + ex.getLocalizedMessage());
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        }
    }

    @Path("RetrieveBookingByPaymentTransaction/{paymentTransactionId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveBookingByPaymentTransaction(@QueryParam("username") String username, @QueryParam("password") String password,
            @PathParam("paymentTransactionId") Long paymentTransactionId) {
        try {
            Customer customer = (Customer) accountSessionBeanLocal.login(username, password);
            
            System.out.println("ws.restful.BookingResource.retrieveBookingById()payment Transaction id = " + paymentTransactionId);
            
            for (TravelItinerary ti : customer.getTravelItineraries()){
                for (Booking bk : ti.getBookings()){
                    if (bk.getPaymentTransaction().getPaymentTransactionId() == paymentTransactionId){
                        bk.cleanRelationships();
                        return Response.status(Status.OK).entity(bk).build();
                    }
                }
            }
            throw new BookingNotFoundException("Unable to find request!");
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        }
    }

    @Path("Update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBooking(BookingHandler objHandler) {
        if (objHandler != null) {
            try {
                Customer customer = (Customer) accountSessionBeanLocal.login(objHandler.getCustomer().getUsername(), objHandler.getPassword());
                System.out.println("ws.restful.BookingResource.updateBooking() booking id = " + objHandler.getBooking().getBookingId());
                System.out.println("ws.restful.BookingResource.updateBooking() Backup booking id = " + objHandler.getBookingId());

                if (objHandler.getBooking().getBookingId() == null) {
                    objHandler.getBooking().setBookingId(objHandler.getBookingId());
                }
                Booking bk = bookingSessionBeanLocal.retrieveBookingById(objHandler.getBooking().getBookingId());

                if (!bk.getTravelItinerary().getCustomer().getCustomerId().equals(customer.getCustomerId())) {
                    throw new CustomerNotMatchException("Please ensure booking matches customer!");
                }
                System.out.println("ws.restful.BookingResource.updateBooking()");
                bookingSessionBeanLocal.updateBooking(objHandler.getBooking());

                Booking bkupdated = bookingSessionBeanLocal.retrieveBookingById(objHandler.getBooking().getBookingId());
                bkupdated.cleanRelationships();

                BigDecimal cost = bookingSessionBeanLocal.getPricingOfBooking(bkupdated.getBookingId(), bkupdated.getStartDate(), bkupdated.getEndDate());
                BookingHandler handler = new BookingHandler();

                handler.setBooking(bkupdated);
                handler.setCost(cost);

                return Response.status(Response.Status.OK).entity(handler).build();

            } catch (Exception ex) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid update request").build();
        }
    }

    @Path("Delete/{bookingId}")
    @DELETE
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBooking(@QueryParam("username") String username,
            @QueryParam("password") String password,
            @PathParam("bookingId") Long bookingId) {
        try {
            Customer customer = (Customer) accountSessionBeanLocal.login(username, password);
            if (!customer.getCustomerId().equals(bookingSessionBeanLocal.retrieveBookingById(bookingId).getTravelItinerary().getCustomer().getCustomerId())) {
                throw new CustomerNotMatchException("Please ensure booking matches customer!");
            }
            bookingSessionBeanLocal.deleteBookingById(bookingId);
            return Response.status(Status.OK).entity(Boolean.TRUE).build();
        } catch (Exception ex) {
            return Response.status(Status.METHOD_NOT_ALLOWED).entity(ex.getMessage()).build();
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

    private AccountSessionBeanLocal lookupAccountSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (AccountSessionBeanLocal) c.lookup("java:global/OptimalTravelPlan/OptimalTravelPlan-ejb/AccountSessionBean!ejb.session.stateless.AccountSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}

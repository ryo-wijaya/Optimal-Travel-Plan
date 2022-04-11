/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.restful;

import ejb.session.stateless.AccountSessionBeanLocal;
import ejb.session.stateless.BookingSessionBeanLocal;
import entity.Customer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
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

    @Path("Update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBooking(BookingHandler objHandler) {
        if (objHandler != null) {
            try {
                Customer customer = (Customer) accountSessionBeanLocal.login(objHandler.getCustomer().getUsername(), objHandler.getPassword());
                if (!objHandler.getBooking().getTravelItinerary().getCustomer().getCustomerId().equals(customer.getCustomerId())){
                    throw new CustomerNotMatchException("Please ensure booking matches customer!");
                }
                bookingSessionBeanLocal.updateBooking(objHandler.getBooking());
                

                return Response.status(Response.Status.OK).entity(Boolean.TRUE).build();

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

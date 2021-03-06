/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.restful;

import ejb.session.stateless.AccountSessionBeanLocal;
import ejb.session.stateless.BookingSessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.SupportRequestSessionBeanLocal;
import entity.Booking;
import entity.Customer;
import entity.Service;
import entity.SupportRequest;
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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import util.exception.AccountDisabledException;
import util.exception.InvalidLoginCredentialException;
import util.exception.SupportRequestNotFoundException;

/**
 * REST Web Service
 *
 * @author sucram
 */
@Path("SupportRequest")
public class SupportRequestResource {

    BookingSessionBeanLocal bookingSessionBean = lookupBookingSessionBeanLocal();

    AccountSessionBeanLocal accountSessionBean = lookupAccountSessionBeanLocal();

    CustomerSessionBeanLocal customerSessionBean = lookupCustomerSessionBeanLocal();

    SupportRequestSessionBeanLocal supportRequestSessionBean = lookupSupportRequestSessionBeanLocal();

    @Context
    private UriInfo context;

    @Path("RetrieveSupportRequest")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveSupportRequest(@QueryParam("username") String username,
            @QueryParam("password") String password) {
        try {
            Customer customer = (Customer) accountSessionBean.login(username, password);
            System.out.println("********** CustomerResource.customerLogin(): Customer " + customer.getUsername() + " login remotely via web service");

            List<SupportRequest> supportRequests = supportRequestSessionBean.retriveSupportRequestsByCustomerId(customer.getAccountId());
            
            for(SupportRequest sr : supportRequests) {
                Service service = sr.getBooking().getService();
                service.cleanSelf();
                sr.getBooking().cleanSelf();
                sr.getBooking().setService(service);
            }

            GenericEntity<List<SupportRequest>> genericEntity = new GenericEntity<List<SupportRequest>>(supportRequests) {
            };

            return Response.status(Status.OK).entity(genericEntity).build();
        } catch (InvalidLoginCredentialException ex) {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @Path("RetrieveSupportRequestById")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveSupportRequestById(@QueryParam("username") String username, 
                                        @QueryParam("password") String password, @QueryParam("supportRequestId") long supportRequestId)
    {
        try
        {
            Customer customer = (Customer) accountSessionBean.login(username, password);
            System.out.println("********** CustomerResource.customerLogin(): Customer " + customer.getUsername() + " login remotely via web service");

            SupportRequest supportRequests = supportRequestSessionBean.retrieveSupportRequestById(supportRequestId);
            
            supportRequests.getBooking().cleanSelf();
            
            GenericEntity<SupportRequest> genericEntity = new GenericEntity<SupportRequest>(supportRequests) {
            };
            
            return Response.status(Status.OK).entity(genericEntity).build();
        }
        catch(InvalidLoginCredentialException ex)
        {            
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @Path("UpdateRequestDetails")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRequestDetails(@QueryParam("username") String username, @QueryParam("password") String password,
            @QueryParam("addRequestDetails") String addRequestDetails, @QueryParam("supportRequestId") Long supportRequestId) {
        try {
            Customer customer = (Customer) accountSessionBean.login(username, password);
            System.out.println("********** CustomerResource.customerLogin(): Customer " + customer.getUsername() + " login remotely via web service");
            
            supportRequestSessionBean.updateSupportRequestDetails(supportRequestId, customer.getAccountId(), addRequestDetails);
            
            return Response.status(Response.Status.OK).entity(Boolean.TRUE).build();
            
        } catch (InvalidLoginCredentialException ex) {
            System.out.println("invalid login");
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        } catch (AccountDisabledException ex) {
            System.out.println("Account disabled");
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        } catch (SupportRequestNotFoundException ex) {
            System.out.println("erro unknown");
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @Path("CreateSupportrequest")
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSupportRequest(@QueryParam("username") String username, @QueryParam("password") String password,
            @QueryParam("requestDetails") String requestDetails, @QueryParam("bookingId") Long bookingId) {
        System.out.println("Test 1: ");
        try {
            Customer customer = (Customer) accountSessionBean.login(username, password);
            System.out.println("********** CustomerResource.customerLogin(): Customer " + customer.getUsername() + " login remotely via web service");
            System.out.println("Test 2 customerlogin: " + customer.getCustomerId());

            Booking booking = bookingSessionBean.retrieveBookingById(bookingId);
            System.out.println("Test 3 bookingID: " + booking.getBookingId());
            Long supportRequestId = supportRequestSessionBean.createNewSupportRequest(new SupportRequest(requestDetails, new Date(), booking), booking.getBookingId());
            //SupportRequest supportRequest = supportRequestSessionBean.retrieveSupportRequestById(supportRequestId);
            System.out.println("Test 4 suppoortreqId: " + supportRequestId);
            return Response.status(Response.Status.OK).entity(supportRequestId).build();
        } catch (InvalidLoginCredentialException ex) {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    public SupportRequestResource() {
    }

    private SupportRequestSessionBeanLocal lookupSupportRequestSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (SupportRequestSessionBeanLocal) c.lookup("java:global/OptimalTravelPlan/OptimalTravelPlan-ejb/SupportRequestSessionBean!ejb.session.stateless.SupportRequestSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private CustomerSessionBeanLocal lookupCustomerSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (CustomerSessionBeanLocal) c.lookup("java:global/OptimalTravelPlan/OptimalTravelPlan-ejb/CustomerSessionBean!ejb.session.stateless.CustomerSessionBeanLocal");
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

    private BookingSessionBeanLocal lookupBookingSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (BookingSessionBeanLocal) c.lookup("java:global/OptimalTravelPlan/OptimalTravelPlan-ejb/BookingSessionBean!ejb.session.stateless.BookingSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

}

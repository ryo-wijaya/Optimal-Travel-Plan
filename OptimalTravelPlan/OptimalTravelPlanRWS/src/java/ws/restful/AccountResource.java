/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.restful;

import ejb.session.stateless.AccountSessionBeanLocal;
import entity.Account;
import entity.Booking;
import entity.Country;
import entity.Customer;
import entity.Service;
import entity.Tag;
import entity.TravelItinerary;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import util.exception.AccountDisabledException;
import util.exception.AccountNotFoundException;
import util.exception.ChangePasswordException;
import util.exception.InvalidLoginCredentialException;
import util.exception.PasswordNotAcceptedException;
import util.exception.UnknownPersistenceException;
import util.exception.UsernameAlreadyExistException;

@Path("Account")
public class AccountResource {

    AccountSessionBeanLocal accountSessionBeanLocal = lookupAccountSessionBeanLocal();

    @Context
    private UriInfo context;

    public AccountResource() {
    }

    @Path("customerLogin")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response customerLogin(@QueryParam("username") String username, @QueryParam("password") String password) {
        try {
            Customer customer = (Customer) accountSessionBeanLocal.login(username, password);
            System.out.println("********** CustomerResource.customerLogin(): Customer " + customer.getUsername() + " login remotely via web service");

            customer.setPassword(null);
            
            for (TravelItinerary ti : customer.getTravelItineraries()) {

                ti.getCountry().getServices().clear();
                
                for (Service service : ti.getCountry().getServices()) {
                    service.setCountry(null);
                }
                ti.getCountry().getServices().clear();
                
                for (Booking booking : ti.getBookings()) {
                    booking.getService().getBookings().clear();
                    booking.setService(null);
                    
                    booking.getReview().setBooking(null);
                    booking.setReview(null);
                    
                    booking.getSupportRequest().setBooking(null);
                    booking.setSupportRequest(null);
                    
                    booking.setPaymentTransaction(null);
                }
            }
            
            for (Tag tag : customer.getFavouriteTags()) {
                for (Service service : tag.getServices()) {
                    service.getTags().clear();
                }
                tag.getServices().clear();
            }

            return Response.status(Status.OK).entity(customer).build();
        } catch (InvalidLoginCredentialException | AccountDisabledException | PasswordNotAcceptedException ex) {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @Path("createCustomerAccount")
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCustomerAccount(@QueryParam("username") String username, @QueryParam("password") String password,
            @QueryParam("name") String name, @QueryParam("mobile") String mobile, @QueryParam("passportNumber") String passportNumber,
            @QueryParam("email") String email, @QueryParam("vaccinationStatus") Boolean vaccinationStatus) {
        try {
            Long id = accountSessionBeanLocal.createNewAccount(new Customer(name, mobile, passportNumber, email, vaccinationStatus, username, password));
            System.out.println("Customer Account created with ID: " + id + " remotely via web service");

            return Response.status(Response.Status.OK).entity(id).build();
        } catch (PasswordNotAcceptedException | UsernameAlreadyExistException | UnknownPersistenceException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @Path("changePassword")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePassword(@QueryParam("username") String username, @QueryParam("password") String password,
            @QueryParam("newPassword") String newPassword) {
        try {
            Customer customer = (Customer) accountSessionBeanLocal.login(username, password);

            accountSessionBeanLocal.changePassword(password, newPassword, customer.getAccountId());
            System.out.println("Changed password occured at customer account: " + customer.getAccountId() + " remotely via web service");

            return Response.status(Response.Status.OK).build();
        } catch (InvalidLoginCredentialException | AccountDisabledException | ChangePasswordException | AccountNotFoundException ex) {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        } catch (PasswordNotAcceptedException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @Path("forgetPasswordChange")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response forgetPasswordChange(@QueryParam("username") String username, @QueryParam("password") String password) {
        try {
            Customer customer = (Customer) accountSessionBeanLocal.login(username, password);

            accountSessionBeanLocal.forgetPasswordChange(customer.getAccountId());
            System.out.println("Forget password change occured at customer account: " + customer.getAccountId() + " remotely via web service");

            return Response.status(Response.Status.OK).build();
        } catch (AccountDisabledException | InvalidLoginCredentialException ex) {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
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

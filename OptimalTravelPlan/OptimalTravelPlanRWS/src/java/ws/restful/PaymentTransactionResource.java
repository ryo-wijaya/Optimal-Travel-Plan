/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.restful;

import ejb.session.stateless.AccountSessionBeanLocal;
import ejb.session.stateless.TransactionSessionBeanLocal;
import entity.Customer;
import entity.PaymentTransaction;
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
import javax.ws.rs.core.Response.Status;
import util.exception.AccountDisabledException;
import util.exception.CustomerNotMatchException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;
import ws.DataModel.PaymentTransactionHandler;

/**
 * REST Web Service
 *
 * @author sucram
 */
@Path("PaymentTransaction")
public class PaymentTransactionResource {

    TransactionSessionBeanLocal transactionSessionBeanLocal = lookupTransactionSessionBeanLocal();

    AccountSessionBeanLocal accountSessionBeanLocal = lookupAccountSessionBeanLocal();

    @Context
    private UriInfo context;

    public PaymentTransactionResource() {
    }

    @Path("Create")
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPaymentTransaction(@QueryParam("username") String username,
            @QueryParam("password") String password,
            @QueryParam("bookingId") Long bookingId,
            @QueryParam("paymentAccountId") Long paymentAccountId) {
        try {
            Customer customer = (Customer) accountSessionBeanLocal.login(username, password);
            PaymentTransaction pt = transactionSessionBeanLocal.makePayment(bookingId, paymentAccountId);

            return Response.status(Response.Status.OK).entity(pt).build();

        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @Path("RetrieveCustomerPaymentTransactions")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllPaymentTransaction(@QueryParam("username") String username,
            @QueryParam("password") String password) {
        try {
            Customer customer = (Customer) accountSessionBeanLocal.login(username, password);
            List<PaymentTransaction> list = transactionSessionBeanLocal.retrievePaymentTransactionsByCustomerId(customer.getCustomerId());
            GenericEntity<List<PaymentTransaction>> genericEntity = new GenericEntity<List<PaymentTransaction>>(list) {
            };
            return Response.status(Status.OK).entity(genericEntity).build();
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

    private TransactionSessionBeanLocal lookupTransactionSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (TransactionSessionBeanLocal) c.lookup("java:global/OptimalTravelPlan/OptimalTravelPlan-ejb/TransactionSessionBean!ejb.session.stateless.TransactionSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}

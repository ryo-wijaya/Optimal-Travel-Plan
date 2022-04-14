/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.restful;

import ejb.session.stateless.AccountSessionBeanLocal;
import ejb.session.stateless.PaymentAccountSessionBeanLocal;
import entity.Customer;
import entity.PaymentAccount;
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
import javax.ws.rs.core.Response.Status;
import util.exception.CustomerNotMatchException;
import ws.DataModel.PaymentAccountHandler;

/**
 * REST Web Service
 *
 * @author sucram
 */
@Path("PaymentAccount")
public class PaymentAccountResource {

    AccountSessionBeanLocal accountSessionBeanLocal = lookupAccountSessionBeanLocal();

    PaymentAccountSessionBeanLocal paymentAccountSessionBeanLocal = lookupPaymentAccountSessionBeanLocal();

    @Context
    private UriInfo context;

    public PaymentAccountResource() {
    }

    @Path("CreatePaymentAccount")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPaymentAccount(PaymentAccountHandler objHandler) {
        System.out.println("Test 1");
        if (objHandler != null) {
            System.out.println("Test 2");
            try {
                Customer customer = (Customer) accountSessionBeanLocal.login(objHandler.getCustomer().getUsername(), objHandler.getPassword());
                
                //Date doesnt work
//                Integer exDate = objHandler.getDate();
//                System.out.println("Test 3");
//                System.out.println(exDate);
//                Date newDate = new Date(exDate);
//                System.out.println("Test 4");
//                objHandler.getPaymentAccount().setCardExpirationDate(newDate);
//                System.out.println("Test 5");

                PaymentAccount paymentAccount = paymentAccountSessionBeanLocal.createNewPaymentAccount(customer.getCustomerId(), objHandler.getPaymentAccount());
                return Response.status(Response.Status.OK).entity(paymentAccount.getPaymenetAccountId()).build();
            } catch (Exception ex) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid update request").build();
        }
    }

    @Path("RetrieveCustomerPaymentAccounts")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllPaymentAccount(@QueryParam("username") String username,
            @QueryParam("password") String password) {
        try {
            Customer customer = (Customer) accountSessionBeanLocal.login(username, password);
            List<PaymentAccount> list = paymentAccountSessionBeanLocal.retrieveAllCustomerPaymentAccounts(customer.getCustomerId());
            GenericEntity<List<PaymentAccount>> genericEntity = new GenericEntity<List<PaymentAccount>>(list) {
            };
            return Response.status(Status.OK).entity(genericEntity).build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @Path("UpdatePaymentAccount")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePaymentAccount(PaymentAccountHandler objHandler) {
        if (objHandler != null) {
            try {
                Customer customer = (Customer) accountSessionBeanLocal.login(objHandler.getCustomer().getUsername(), objHandler.getPassword());
                if (!verifyPaymentAccountToCustomer(customer, objHandler.getPaymentAccount())) {
                    throw new CustomerNotMatchException("Please ensure customer owns this payment account!");
                }
                paymentAccountSessionBeanLocal.updatePaymentAccount(objHandler.getPaymentAccount());
                return Response.status(Response.Status.OK).entity(Boolean.TRUE).build();
            } catch (Exception ex) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid update request").build();
        }
    }

    @Path("DeletePaymentAccount")
    @DELETE
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePaymentAccount(@QueryParam("username") String username,
            @QueryParam("password") String password,
            @QueryParam("paymentAccountId") Long paymentAccountId) {
        try {
            System.out.println("Test 1");
            Customer customer = (Customer) accountSessionBeanLocal.login(username, password);
            System.out.println("Test 2");
            PaymentAccount pa = paymentAccountSessionBeanLocal.retrievePaymentAccountByPaymentAccountId(paymentAccountId);
            System.out.println("Test 3");
            if (!verifyPaymentAccountToCustomer(customer, pa)) {
                System.out.println("Test 4");
                throw new CustomerNotMatchException("Please ensure customer owns this payment account!");
            }
            System.out.println("Test 5");
            paymentAccountSessionBeanLocal.toggleAccountStatus(paymentAccountId);
            System.out.println("Test 6");
            return Response.status(Status.OK).build();
        } catch (Exception ex) {
            return Response.status(Status.METHOD_NOT_ALLOWED).entity(ex.getMessage()).build();
        }
    }

    private boolean verifyPaymentAccountToCustomer(Customer customer, PaymentAccount pa) {
        for (PaymentAccount paymentAccount : customer.getPaymentAccounts()) {
            if (pa.getPaymenetAccountId().equals(paymentAccount.getPaymenetAccountId())) {
                return true;
            }
        }
        return false;
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

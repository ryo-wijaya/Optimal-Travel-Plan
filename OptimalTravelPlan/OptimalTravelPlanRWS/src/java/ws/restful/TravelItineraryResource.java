/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.restful;

import ejb.session.stateless.AccountSessionBeanLocal;
import ejb.session.stateless.TravelItinerarySessionBeanLocal;
import entity.Customer;
import entity.TravelItinerary;
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
import ws.DataModel.TravelItineraryHandler;

/**
 * REST Web Service
 *
 * @author sucram
 */
@Path("TravelItinerary")
public class TravelItineraryResource {

    TravelItinerarySessionBeanLocal travelItinerarySessionBeanLocal = lookupTravelItinerarySessionBeanLocal();

    AccountSessionBeanLocal accountSessionBeanLocal = lookupAccountSessionBeanLocal();

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
                Customer customer = (Customer) accountSessionBeanLocal.login(objHandler.getCustomer().getUsername(), objHandler.getPassword());
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
                if (!travelItinerary.getCustomer().getAccountId().equals(customer.getAccountId())) {
                    throw new CustomerNotMatchException("Please ensure travel itinerary matches customer!");
                }
                travelItinerary = travelItinerarySessionBeanLocal.updateTravelItinerary(travelItinerary);
                travelItinerary.cleanRelationships();

                return Response.status(Response.Status.OK).entity(travelItinerary).build();

            } catch (Exception ex) {
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
            @QueryParam("password") String password,
            @PathParam("travelItineraryId") Long travelItineraryId) {
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

    @Path("RecommendTravelItinerary")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response recommendTravelItinerary(@QueryParam("username") String username,
            @QueryParam("password") String password,
            @PathParam("travelItineraryId") Long travelItineraryId) {
        try {
            Customer customer = (Customer) accountSessionBeanLocal.login(username, password);
            if (!customer.getCustomerId().equals(travelItinerarySessionBeanLocal.retrieveTravelItineraryById(travelItineraryId).getCustomer().getCustomerId())) {
                throw new CustomerNotMatchException("Please ensure travel itinerary matches customer!");
            }
            TravelItinerary ti = travelItinerarySessionBeanLocal.retrieveTravelItineraryById(travelItineraryId);
            travelItinerarySessionBeanLocal.recommendTravelItinerary(ti);
            ti.cleanRelationships();
            return Response.status(Status.OK).entity(ti).build();
        } catch (InvalidLoginCredentialException ex) {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        } catch (Exception ex) {
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
            return Response.status(Status.OK).build();
        } catch (Exception ex) {
            return Response.status(Status.METHOD_NOT_ALLOWED).entity(ex.getMessage()).build();
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
}

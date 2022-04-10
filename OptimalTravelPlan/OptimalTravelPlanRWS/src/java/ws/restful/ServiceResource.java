/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.restful;

import ejb.session.stateless.AccountSessionBeanLocal;
import ejb.session.stateless.ServiceSessionBeanLocal;
import entity.Booking;
import entity.Customer;
import entity.Service;
import entity.Tag;
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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.AccountDisabledException;
import util.exception.InvalidLoginCredentialException;
import util.exception.PasswordNotAcceptedException;
import ws.DataModel.ServiceByTagHandler;


/**
 * REST Web Service
 *
 * @author sucram
 */
@Path("Service")
public class ServiceResource {

    AccountSessionBeanLocal accountSessionBeanLocal = lookupAccountSessionBeanLocal();

    ServiceSessionBeanLocal serviceSessionBeanLocal = lookupServiceSessionBeanLocal();
    
    
    
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ServiceResource
     */
    public ServiceResource() {
    }
    
    @Path("retrieveAllActiveServices")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllActiveServices(@QueryParam("username") String username, @QueryParam("password") String password) {
        try {
            Customer customer = (Customer) accountSessionBeanLocal.login(username, password);
            
            List<Service> services = serviceSessionBeanLocal.retrieveAllActiveServices();
            
            for (Service service : services) {
                for (Booking booking : service.getBookings()) {
                    booking.setService(null);
                }
                service.getBookings().clear();
                
                for (Tag tag : service.getTags()) {
                    tag.getServices().clear();
                }
                service.getTags().clear();
            }
            
            GenericEntity<List<Service>> genericEntityServices = new GenericEntity<List<Service>>(services) {};

            return Response.status(Response.Status.OK).entity(genericEntityServices).build();
        } catch (AccountDisabledException | InvalidLoginCredentialException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @Path("retrieveAllActiveEntertainment")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllActiveEntertainment(@QueryParam("username") String username, @QueryParam("password") String password) {
        try {
            Customer customer = (Customer) accountSessionBeanLocal.login(username, password);
            
            List<Service> services = serviceSessionBeanLocal.retrieveAllEntertainment();
            
            for (Service service : services) {
                for (Booking booking : service.getBookings()) {
                    booking.setService(null);
                }
                service.getBookings().clear();
                
                for (Tag tag : service.getTags()) {
                    tag.getServices().clear();
                }
                service.getTags().clear();
            }
            
            GenericEntity<List<Service>> genericEntityServices = new GenericEntity<List<Service>>(services) {};

            return Response.status(Response.Status.OK).entity(genericEntityServices).build();
        } catch (AccountDisabledException | InvalidLoginCredentialException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @Path("retrieveAllActiveServiceByCountryId")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllActiveServiceByCountryId(@QueryParam("username") String username, @QueryParam("password") String password, 
            @QueryParam("countryId") Long countryId) {
        try {
            Customer customer = (Customer) accountSessionBeanLocal.login(username, password);
            
            List<Service> services = serviceSessionBeanLocal.retrieveAllActiveServiceByCountry(countryId);
            
            for (Service service : services) {
                for (Booking booking : service.getBookings()) {
                    booking.setService(null);
                }
                service.getBookings().clear();
                
                for (Tag tag : service.getTags()) {
                    tag.getServices().clear();
                }
                service.getTags().clear();
            }
            
            GenericEntity<List<Service>> genericEntityServices = new GenericEntity<List<Service>>(services) {};

            return Response.status(Response.Status.OK).entity(genericEntityServices).build();
        } catch (AccountDisabledException | InvalidLoginCredentialException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @Path("retrieveAllActiveServiceByBusinessId")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllActiveServiceByBusinessId(@QueryParam("username") String username, @QueryParam("password") String password, 
            @QueryParam("businessId") Long businessId) {
        try {
            Customer customer = (Customer) accountSessionBeanLocal.login(username, password);
            
            List<Service> services = serviceSessionBeanLocal.retrieveAllActiveServiceByBusinessId(businessId);
            
            for (Service service : services) {
                for (Booking booking : service.getBookings()) {
                    booking.setService(null);
                }
                service.getBookings().clear();
                
                for (Tag tag : service.getTags()) {
                    tag.getServices().clear();
                }
                service.getTags().clear();
            }
            
            GenericEntity<List<Service>> genericEntityServices = new GenericEntity<List<Service>>(services) {};

            return Response.status(Response.Status.OK).entity(genericEntityServices).build();
        } catch (AccountDisabledException | InvalidLoginCredentialException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @Path("retrieveAllActiveServiceByTags")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllActiveServiceByTags(ServiceByTagHandler dataWrapper) {
        try {
            Customer customer = (Customer) accountSessionBeanLocal.login(dataWrapper.getUsername(), dataWrapper.getPassword());
            
            List<Service> services = serviceSessionBeanLocal.retrieveAllActiveServiceByTags(dataWrapper.getTagIds());
            
            for (Service service : services) {
                for (Booking booking : service.getBookings()) {
                    booking.setService(null);
                }
                service.getBookings().clear();
                
                for (Tag tag : service.getTags()) {
                    tag.getServices().clear();
                }
                service.getTags().clear();
            }
            
            GenericEntity<List<Service>> genericEntityServices = new GenericEntity<List<Service>>(services) {};

            return Response.status(Response.Status.OK).entity(genericEntityServices).build();
        } catch (AccountDisabledException | InvalidLoginCredentialException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    private ServiceSessionBeanLocal lookupServiceSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (ServiceSessionBeanLocal) c.lookup("java:global/OptimalTravelPlan/OptimalTravelPlan-ejb/ServiceSessionBean!ejb.session.stateless.ServiceSessionBeanLocal");
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

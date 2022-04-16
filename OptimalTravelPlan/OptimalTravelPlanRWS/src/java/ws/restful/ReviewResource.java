/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.restful;

import ejb.session.stateless.AccountSessionBeanLocal;
import ejb.session.stateless.BookingSessionBeanLocal;
import ejb.session.stateless.ReviewSessionBeanLocal;
import entity.Booking;
import entity.Customer;
import entity.PaymentTransaction;
import entity.Review;
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
import util.exception.BookingNotMatchException;
import util.exception.CustomerNotMatchException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;
import ws.DataModel.ReviewHandler;

/**
 * REST Web Service
 *
 * @author sucram
 */
//ReviewSessionBean: retrieveReviewByServiceId
@Path("Review")
public class ReviewResource {

    AccountSessionBeanLocal accountSessionBeanLocal = lookupAccountSessionBeanLocal();

    @Context
    private UriInfo context;

    public ReviewResource() {
    }

    ReviewSessionBeanLocal reviewSessionBeanLocal = lookupReviewSessionBeanLocal();

    BookingSessionBeanLocal bookingSessionBeanLocal = lookupBookingSessionBeanLocal();

    @Path("Create")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createReview(ReviewHandler objHandler) {
        if (objHandler != null) {
            try {
                System.out.println("ws.restful.ReviewResource.createReview()");
                Customer customer = (Customer) accountSessionBeanLocal.login(objHandler.getCustomer().getUsername(), objHandler.getPassword());

                Booking booking = bookingSessionBeanLocal.retrieveBookingById(objHandler.getBookingId());
                if (!booking.getTravelItinerary().getCustomer().getCustomerId().equals(customer.getCustomerId())) {
                    throw new CustomerNotMatchException("Please ensure booking matches customer!");
                }

                Long reviewId = reviewSessionBeanLocal.createNewReview(
                        objHandler.getBookingId(),
                        objHandler.getReview()).getReviewId();

                return Response.status(Response.Status.OK).entity(reviewId).build();

            } catch (Exception ex) {
                ex.printStackTrace();
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
    public Response updateReview(ReviewHandler objHandler) {
        if (objHandler != null) {
            try {
                System.out.println("ws.restful.ReviewResource.updateReview()");
                Customer customer = (Customer) accountSessionBeanLocal.login(objHandler.getCustomer().getUsername(), objHandler.getPassword());

                Review review = reviewSessionBeanLocal.retrieveReviewByReviewId(objHandler.getReviewId());
                if (!review.getBooking().getTravelItinerary().getCustomer().getCustomerId().equals(customer.getCustomerId())) {
                    throw new CustomerNotMatchException("Please ensure booking matches customer!");
                }
                review = objHandler.getReview();
                if (review.getReviewId() == null) {
                    review.setReviewId(objHandler.getReviewId());
                }

                reviewSessionBeanLocal.updateReview(review);
                review = reviewSessionBeanLocal.retrieveReviewByReviewId(objHandler.getReviewId());
                review.cleanRelationships();

                return Response.status(Response.Status.OK).entity(review).build();

            } catch (Exception ex) {
                ex.printStackTrace();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid update request").build();
        }
    }

    @Path("Delete/{reviewId}")
    @DELETE
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteReview(@QueryParam("username") String username,
            @QueryParam("password") String password,
            @PathParam("reviewId") Long reviewId) {
        try {
            Customer customer = (Customer) accountSessionBeanLocal.login(username, password);
            if (!reviewSessionBeanLocal.retrieveReviewByReviewId(reviewId).getBooking().getTravelItinerary().getCustomer().getCustomerId().equals(customer.getCustomerId())) {
                throw new BookingNotMatchException("Please ensure this booking belongs to customer!");
            }
            reviewSessionBeanLocal.deleteReview(reviewId);
            return Response.status(Status.OK).entity(Boolean.TRUE).build();
        } catch (Exception ex) {
            return Response.status(Status.METHOD_NOT_ALLOWED).entity(ex.getMessage()).build();
        }
    }

    @Path("retrieveReviewsByServiceId")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveReviewsByServiceId(@QueryParam("username") String username,
            @QueryParam("password") String password, @QueryParam("serviceId") Long serviceId) {
        try {
            Customer customer = (Customer) accountSessionBeanLocal.login(username, password);
            List<Review> list = reviewSessionBeanLocal.retrieveReviewsByServiceId(serviceId);

            for (Review review : list) {
                review.getBooking().setSupportRequest(null);
                review.getBooking().setService(null);
                review.getBooking().setPaymentTransaction(null);
                review.getBooking().getTravelItinerary().setCountry(null);
                review.getBooking().getTravelItinerary().getCustomer().getPaymentAccounts().clear();
                review.getBooking().getTravelItinerary().getCustomer().getFavouriteTags().clear();
                review.getBooking().getTravelItinerary().getCustomer().getTravelItineraries().clear();
                review.getBooking().getTravelItinerary().getBookings().clear();
                review.getBooking().setReview(null);
                System.out.println("Review " + review.getReviewId());
            } 

            GenericEntity<List<Review>> genericEntity = new GenericEntity<List<Review>>(list) {
            };

            return Response.status(Status.OK).entity(genericEntity).build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
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

    private ReviewSessionBeanLocal lookupReviewSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (ReviewSessionBeanLocal) c.lookup("java:global/OptimalTravelPlan/OptimalTravelPlan-ejb/ReviewSessionBean!ejb.session.stateless.ReviewSessionBeanLocal");
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

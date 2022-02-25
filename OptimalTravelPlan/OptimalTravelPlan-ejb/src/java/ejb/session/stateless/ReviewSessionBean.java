/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Booking;
import entity.Review;
import entity.Service;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.BookingNotFoundException;
import util.exception.ConstraintViolationException;
import util.exception.ReviewNotFoundException;
import util.exception.UnknownPersistenceException;

@Stateless
public class ReviewSessionBean implements ReviewSessionBeanLocal {

    @EJB
    private BookingSessionBeanLocal bookingSessionBean;

    @PersistenceContext(unitName = "OptimalTravelPlan-ejbPU")
    private EntityManager em;
    
    
    @Override
    public Review createNewReview(Long bookingId, Review review) throws BookingNotFoundException, UnknownPersistenceException, ConstraintViolationException {
        try {
            Booking booking = bookingSessionBean.retrieveBookingById(bookingId);
            review.setBooking(booking);
            em.persist(review);
            booking.setReview(review);
            em.flush();
            
            //cumulating ratings
            Service service = booking.getService();
            Integer serviceOldRatingNum = service.getTotalNumOfRatings();
            service.setRating(((service.getRating() * serviceOldRatingNum) + review.getRating()) / (serviceOldRatingNum + 1));
            service.setTotalNumOfRatings(serviceOldRatingNum + 1);
            return review;
            
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new ConstraintViolationException();
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }
    
    @Override
    public List<Review> retrieveAllReview() {
        Query query = em.createQuery("SELECT r FROM Review r");
        List<Review> reviewEntities = query.getResultList();
        
        for(Review reviewEntity:reviewEntities) {            
            reviewEntity.getBooking();
        }
        return reviewEntities;
    } 
    
    @Override
    public Review retrieveReviewByReviewId(Long reviewId) throws ReviewNotFoundException {
        Review review = em.find(Review.class, reviewId);
        if(review != null) {
            return review;
        } else {
            throw new ReviewNotFoundException("Review ID " + reviewId + " does not exist!");
        }
    }
    
    @Override
    public void deleteReview(Long reviewId) throws ReviewNotFoundException {
        Review review = this.retrieveReviewByReviewId(reviewId);
        review.getBooking().setReview(null);
        em.remove(review);
    }
    
    @Override
    public List<Review> retrieveReviewsByServiceId(Long serviceId) {
        Query query = em.createQuery("SELECT r FROM Review r JOIN r.booking b JOIN b.service s WHERE s.serviceId = :serviceId");
        query.setParameter("serviceId", serviceId);
        return query.getResultList();
    }
    
    @Override
    public void updateReview(Review review) throws ReviewNotFoundException {
        if (review != null && review.getReviewId()!= null) {
            Review reviewToUpdate = this.retrieveReviewByReviewId(review.getReviewId());

            reviewToUpdate.setRating(review.getRating());
            reviewToUpdate.setContent(review.getContent());
            reviewToUpdate.setBusinessReply(review.getBusinessReply());
            
        } else {
            throw new ReviewNotFoundException("Review ID not provided for review to be updated");
        }
    }
    //do one update for costomer (content + rating
    //do one update for business business reply
}

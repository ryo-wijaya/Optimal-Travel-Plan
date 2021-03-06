/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Review;
import java.util.List;
import javax.ejb.Local;
import util.exception.BookingNotFoundException;
import util.exception.ConstraintViolationException;
import util.exception.ReviewNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author sucram
 */
@Local
public interface ReviewSessionBeanLocal {

    public Review createNewReview(Long bookingId, Review review) throws BookingNotFoundException, UnknownPersistenceException, ConstraintViolationException;

    public List<Review> retrieveReviewsByServiceId(Long serviceId);

    public void updateReview(Review review) throws ReviewNotFoundException;

    public Review retrieveReviewByReviewId(Long reviewId) throws ReviewNotFoundException;

    public void deleteReview(Long reviewId) throws ReviewNotFoundException;

    public List<Review> retrieveAllReview();

    public List<Review> retrieveReviewsByBusinessId(Long businessId);
    
}

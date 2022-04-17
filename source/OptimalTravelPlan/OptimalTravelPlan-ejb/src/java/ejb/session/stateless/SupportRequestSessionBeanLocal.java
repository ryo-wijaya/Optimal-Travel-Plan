/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.SupportRequest;
import java.util.List;
import javax.ejb.Local;
import util.exception.ConstraintViolationException;
import util.exception.CreateSupportRequestException;
import util.exception.ResolveSupportRequestException;
import util.exception.SupportRequestNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author sucram
 */
@Local
public interface SupportRequestSessionBeanLocal {

    public SupportRequest retrieveSupportRequestById(Long supportRequestId) throws SupportRequestNotFoundException;

    public Long createNewSupportRequest(SupportRequest newSupportRequest, Long bookingId) throws UnknownPersistenceException, ConstraintViolationException, CreateSupportRequestException;

    public List<SupportRequest> retrieveAllSupportRequests();

    public List<SupportRequest> retrieveAllUnresolvedSupportRequests();

    public void resolveSupportRequest(Long supportRequestId) throws SupportRequestNotFoundException, ResolveSupportRequestException;

    public SupportRequest updateSupportRequestDetails(Long supportRequestId, Long AccountId, String comments) throws SupportRequestNotFoundException;

    public String getFormattedComment(String name);

    public List<SupportRequest> retriveSupportRequestsByCustomerId(Long customerId);
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Business;
import java.util.List;
import javax.ejb.Local;
import util.exception.AccountNotFoundException;
import util.exception.DeleteBusinessException;
import util.exception.InvalidLoginCredentialsException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateBusinessException;
import util.exception.UsernameAlreadyExistException;

/**
 *
 * @author ryo20
 */
@Local
public interface BusinessSessionBeanLocal {

    public List<Business> retrieveAllBusinesses();

    public Business retrieveBusinessById(Long businessId) throws AccountNotFoundException;

    public Business retrieveBusinessByUsername(String username) throws AccountNotFoundException;

    public void updateBusiness(Business business) throws AccountNotFoundException, UpdateBusinessException;

    public void deleteBusiness(Long businessId) throws AccountNotFoundException, DeleteBusinessException;

    public Business createNewBusiness(Business newBusiness)throws UsernameAlreadyExistException, UnknownPersistenceException;

    public Business retrieveBusinessByEmail(String email) throws AccountNotFoundException;
}

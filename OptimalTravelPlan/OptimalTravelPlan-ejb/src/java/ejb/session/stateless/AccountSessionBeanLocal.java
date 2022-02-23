/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Account;
import javax.ejb.Local;
import util.exception.AccountNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UsernameAlreadyExistException;

/**
 *
 * @author sucram
 */
@Local
public interface AccountSessionBeanLocal {

    public Long createNewAccount(Account newAccount) throws UsernameAlreadyExistException, UnknownPersistenceException;

    public void toggleAccountStatus(Long accountId) throws AccountNotFoundException;
    
}
